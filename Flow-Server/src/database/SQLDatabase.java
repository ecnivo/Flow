package database;

import server.DataManagement;
import server.FlowServer;
import util.DatabaseException;
import util.Results;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Handles all interfacing with the SQLite Database. Includes various prepared
 * SQL statements to allow for easy access to SQLite database functionality.<br>
 * Duties include:
 * <ul>
 * <li>User accounts</li>
 * <li>Sessions and logged in users</li>
 * <li>Project meta data and permissions</li>
 * <li>Name collision prevention</li>
 * <li>Directory / file / version meta data</li>
 * <li>Corruption prevention</li>
 * </ul>
 *
 * @author Bimesh De Silva
 * @version January 19th, 2016
 */
public class SQLDatabase {

    /**
     * Location where the SQLite JDBC drivers are stored
     */
    private static final String DRIVER = "org.sqlite.JDBC";

    /**
     * Number of seconds to allow for searching before timeout (this is a safety
     * net, as the socket times out before this)
     */
    private static final int TIMEOUT = 2;

    /**
     * Access levels used for projects
     */
    private static final int NONE = 0;
    public static final int VIEW = 1;
    public static final int EDIT = 2;
    public static final int OWNER = 3;

    /**
     * Used to signify the different types of possible documents.
     */
    private static final String ARBITRARY_DOCUMENT = "ARBITRARY_DOCUMENT";
    public static final String TEXT_DOCUMENT = "TEXT_DOCUMENT";

    /**
     * Represents to location where all save data is stored relative to the
     * project directory, including the users, their documents, and the database
     * file. The backup copy is for use by the {@link copyFileSystem()} method
     * is recovering a corrupted database.
     */
    public static final String BACKUP_FOLDER = "backup";
    public static final String LIVE_FOLDER = "data";

    /**
     * Represents the location of the database file relative to the project
     * directory.
     */
    public static final String BACKUP_DATABASE = BACKUP_FOLDER + File.separator + "FlowDatabse.db";
    public static final String LIVE_DATABASE = LIVE_FOLDER + File.separator + "FlowDatabse.db";

    /**
     * Folders for various stored data elements
     */
    public static final String LIVE_USERS = LIVE_FOLDER + File.separator + "users";
    public static final String LIVE_FILES = LIVE_FOLDER + File.separator + "files";

    /**
     * Connection to the database.
     */
    private Connection connection;

    /**
     * Latest instance of the SQLDatabase
     */
    private static SQLDatabase instance;

    private static final Logger LOGGER = Logger.getLogger("FLOW");

    /**
     * Verifies integrity of the database file, file system, and synchronization
     * between the two. Tries to fix issues, but if not possible, reloads all
     * files from the backup.
     */
    private SQLDatabase() {
        // Load up the JDBC driver
        try {
            DriverManager.registerDriver((Driver) Class.forName(DRIVER).newInstance());
        } catch (Exception e) {
            LOGGER.severe("Error loading database driver: " + e.toString());
            return;
        }

        // Check for corruption in the database and file system, try to repair,
        // if not possible, recover from backup.
        if ((!this.checkForDatabaseCorruption(BACKUP_DATABASE)) || (!this.checkAndRepairFileSystemCorruption(LIVE_FOLDER))) {
            try {
                this.recoverFileSystem(BACKUP_FOLDER);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Ends all sessions which may have not been properly ended when the
        // server was last shut down
        try {
            this.refreshSessions();
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.severe("Error refreshing sessions");
        }
    }

    /**
     * Returns the latest instance of the SQLDatabase.
     *
     * @return the latest instance of the SQLDatabase, or a new SQLDatabse
     * object if one has yet to be initialized.
     */
    public static SQLDatabase getInstance() {
        if (instance == null)
            instance = new SQLDatabase();
        return instance;
    }

    /**
     * Getter for all projects associated with the specified username,
     * completely ignoring whether the user is the owner, or has only edit or
     * view access.
     *
     * @param username the ID of the user.
     * @return all projects associated with the specified username.
     * @throws DatabaseException if there is an error accessing the database.
     */
    public ResultSet getProjects(String username) throws DatabaseException {
        try {
            // Note: deleted the checking code, since a user can have no
            // projects, which would throw an error, checking code moved to
            // external method
            return this.query(String.format("SELECT * FROM access WHERE Username = '%s';", username));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException(FlowServer.ERROR);
        }
    }

    /**
     * Specifies the access level to a specific project for a user. <br>
     * <br>
     * <b>***WARNING***:</b> This method should only be used when creating a new
     * project, as if has no preventive measure that protect against rendering
     * projects owner-less (which is an illegal state for any project in the
     * SQLDatabase).
     *
     * @param accessLevel the level of access provided to the user, either {@link OWNER}
     *                    , {@link EDIT}, or {@link VIEW}.
     * @param projectId   the project which to provide the user access to.
     * @param username    the username which to provide access to.
     * @return whether or not the access was successfully granted.
     */
    private String updateAccess(int accessLevel, String projectId, String username) {
        try {
            // Check if user exists
            if (this.userExists(username)) {

                // Remove any old access status
                this.update(String.format("DELETE FROM access WHERE Username = '%s' AND ProjectID = '%s';", username, projectId));
                //
                if (accessLevel == EDIT || accessLevel == VIEW) {
                    this.update(String.format("INSERT INTO access values('%s', '%s', '%s');", projectId, username, accessLevel));
                } else if (accessLevel == OWNER) {
                    // Changes the owner of the project in the projects table
                    this.update(String.format("UPDATE projects SET OwnerUsername = '%s' WHERE ProjectID = '%s';", username, projectId));

                    // Change permissions of old owner
                    this.update(String.format("UPDATE Access SET AccessLevel = '%d' WHERE ProjectID = '%s' AND AccessLevel = '%d';", EDIT, projectId, OWNER));

                    // Changes the permissions of the user to be an owner
                    this.update("INSERT INTO access values('" + projectId + "', '" + username + "', '" + OWNER + "');");
                } else if (accessLevel == NONE) {
                    this.update(String.format("DELETE FROM access WHERE Username = '%s' AND ProjectID = '%s';", username, projectId));
                } else {
                    return "ACCESS_LEVEL_INVALID";
                }
            } else {
                return "USERNAME_DOES_NOT_EXIST";
            }
        } catch (SQLException | DatabaseException e) {
            // The only possible DatabaseException that could be thrown here is
            // from userExists(), which could only use FlowServer#ERROR for the
            // exception message
            e.printStackTrace();
            return FlowServer.ERROR;
        }
        return "OK";
    }

    /**
     * Less restrictive method for updating access to a project. Designed to be
     * used when changes are requested by the <b>owner</b> of a project.
     *
     * @param accessLevel the access level.
     * @param projectUUID the string representation of the UUID of the project.
     * @param username    the UUID of the user.
     * @return the status of the operation.
     * @throws DatabaseException if there is an error accessing the database.
     */
    public String restrictedUpdateAccess(int accessLevel, String projectUUID, String username) throws DatabaseException {
        try {
            ResultSet data = this.query(String.format("SELECT OwnerUsername FROM Projects WHERE ProjectID = '%s';", projectUUID));
            if (data.next()) {
                if (accessLevel == OWNER || username.equals(data.getString("OwnerUsername"))) {
                    return "ACCESS_DENIED";
                }
                return this.updateAccess(accessLevel, projectUUID, username);
            }
            return "INVALID_PROJECT_UUID";
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
    }

    /**
     * Restrictive method for updating access to a project. Designed to prevent
     * changes that may have an effect on the ownership or the owner's access to
     * the project.
     *
     * @param accessLevel the access level.
     * @param projectUUID the string representation of the UUID of the project.
     * @param username    the UUID of the user.
     * @return the status of the operation.
     * @throws DatabaseException if there is an error accessing the database.
     */
    public String ownerUpdateAccess(int accessLevel, String projectUUID, String username) throws DatabaseException {
        try {
            ResultSet data = this.query(String.format("SELECT OwnerUsername FROM Projects WHERE ProjectID = '%s';", projectUUID));
            if (data.next()) {
                if (username.equals(data.getString("OwnerUsername"))) {
                    return "ACCESS_DENIED";
                }
                return this.updateAccess(accessLevel, projectUUID, username);
            }
            return "INVALID_PROJECT_UUID";
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
    }

    /**
     * Getter for all files associated with the specified project.
     *
     * @param projectUUID the string representation of the UUID of the project.
     * @return all associated files.
     * @throws DatabaseException if there is an error accessing the database.
     */
    public ResultSet getFilesInProject(String projectUUID) throws DatabaseException {
        try {
            return this.query(String.format("SELECT * FROM documents WHERE ProjectID = '%s';", projectUUID));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException(FlowServer.ERROR);
        }
    }

    /**
     * Getter for all files inside the specified directory.
     *
     * @param directoryUUID the string representation of the UUID of the directory.
     * @return all associated files.
     * @throws DatabaseException if there is an error accessing the database.
     */
    public ResultSet getFilesInDirectory(String directoryUUID) throws DatabaseException {
        try {
            return this.query(String.format("SELECT * FROM documents WHERE ParentDirectoryID = '%s';", directoryUUID));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException(FlowServer.ERROR);
        }
    }

    /**
     * Getter for all directories inside the specified directory.
     *
     * @param directoryUUID the UUID of the directory in String form.
     * @return all associated files.
     */
    public ResultSet getDirectoriesInDirectory(String directoryUUID) throws DatabaseException {
        try {
            return this.query(String.format("SELECT * FROM directories WHERE ParentDirectoryID = '%s';", directoryUUID));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException(FlowServer.ERROR);
        }
    }

    /**
     * Creates a new project with the specified name, owner, and UUID.
     *
     * @param projectName the name of the project.
     * @param username    username of the user who created the project.
     * @param projectUUID the string representation of the UUID of the project.
     * @return the status of the operation.
     */
    public String newProject(String projectUUID, String projectName, String username) {
        try {
            if (this.query(String.format("SELECT * FROM Projects WHERE ProjectName = '%s' and OwnerUsername = '%s';", projectName, username)).next()) {
                return "PROJECT_NAME_INVALID";
            }
            this.update(String.format("INSERT INTO projects VALUES('%s', '%s', '%s');", projectUUID, projectName, username));
        } catch (SQLException e) {
            e.printStackTrace();
            return FlowServer.ERROR;
        }
        String status = this.newDirectory(projectName, projectUUID, projectUUID, projectUUID);
        if (status.equals("OK"))
            return this.updateAccess(SQLDatabase.OWNER, projectUUID, username);
        return status;
    }

    /**
     * Creates a new file within the specified project.
     *
     * @param fileUUID      the ID of the file ({@link UUID#toString() string
     *                      representation} of the UUID associated with file).
     * @param fileName      the name of the file (including the extension).
     * @param projectUUID   the ID of the project which to place the file inside
     * @param directoryUUID the ID of the directory which to place the file inside
     */
    public String newFile(String fileUUID, String fileName, String projectUUID, String directoryUUID) {
        try {
            if (this.query(String.format("SELECT * FROM Documents WHERE ParentDirectoryID = '%s' AND DocumentName = '%s';", directoryUUID, fileName)).next()) {
                return "FILE_NAME_INVALID";
            }
            if ("TEXT_DOCUMENT".equals(ARBITRARY_DOCUMENT) || "TEXT_DOCUMENT".equals(TEXT_DOCUMENT)) {
                this.update(String.format("INSERT INTO documents VALUES('%s', '%s', '%s', '%s', '%s');", fileUUID, projectUUID, fileName, directoryUUID, "TEXT_DOCUMENT"));
            } else {
                // This cannot be the client's error as the type is determined
                // by the request type (new_arbitrarydocument and
                // new_textdocument), rather than a passed value.
                return FlowServer.ERROR;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return FlowServer.ERROR;
        }
        return "OK";
    }

    /**
     * Creates a new directory within the specified project.
     *
     * @param directoryName     the name of the directory.
     * @param directoryId       the ID of the directory ({@link UUID#toString() string
     *                          representation} of the UUID associated with the directory).
     * @param projectId         the ID of the project which to place the directory inside.
     * @param parentDirectoryId the ID of the directory which to place the directory inside.
     */
    public String newDirectory(String directoryName, String directoryId, String projectId, String parentDirectoryId) {
        try {
            // Prevent two directories from having the same same if they share
            // the same parent directory
            if (this.query(String.format("SELECT * FROM Directories WHERE ParentDirectoryID = '%s' AND DirectoryName = '%s';", parentDirectoryId, directoryName)).next()) {
                return "DIRECTORY_NAME_INVALID";
            }
            this.update(String.format("INSERT INTO directories VALUES('%s', '%s', '%s', '%s');", directoryId, parentDirectoryId.equals(directoryId) ? "null" : parentDirectoryId, directoryName, projectId));
        } catch (SQLException e) {
            e.printStackTrace();
            return FlowServer.ERROR;
        }
        return "OK";
    }

    /**
     * Creates a new version of the specified file with the specified version
     * UUID.
     *
     * @param fileUUID    the string representation of the UUID of the file.
     * @param versionUUID the string representation of the UUID of the version.
     * @return the status of the request, either 'OK' or
     * {@link FlowServer#ERROR}
     */
    public String newVersion(String fileUUID, String versionUUID) {
        try {
            this.update(String.format("INSERT INTO Versions VALUES('%s', '%d', '%s');", versionUUID, new Date().getTime(), fileUUID));
        } catch (SQLException e) {
            e.printStackTrace();
            return FlowServer.ERROR;
        }
        return "OK";
    }

    /**
     * Getter for all of the usernames in the database who have access to the
     * specified project.
     *
     * @return all of the usernames in the database.
     * @throws DatabaseException
     */
    public ResultSet getUserNames(String projectId) throws DatabaseException {
        try {
            if (!this.query(String.format("SELECT * from projects WHERE ProjectID = '%s';", projectId)).next()) {
                throw new DatabaseException("PROJECT_DOES_NOT_EXIST");
            }
            return this.query(String.format("SELECT Username FROM access WHERE ProjectID = '%s';", projectId));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new DatabaseException(FlowServer.ERROR);
    }

    /**
     * Authenticates a user by verifying if the specified username and password
     * pair exists in the database.
     *
     * @param username the user's username.
     * @param password the user's encrypted password.
     * @return whether or not the username and password exists in the database.
     */
    public boolean authenticate(String username, String password) {
        try {
            ResultSet pair = this.query(String.format("SELECT Password FROM users WHERE Username = '%s';", username));
            if (pair.next()) {
                return password.equals(pair.getString("password"));
            }
        } catch (SQLException e) {
            LOGGER.severe("Error authenticating user: " + username + " with password: " + password);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Creates a new session for the specified username and serial number.
     *
     * @param username  the UUID of the user.
     * @param sessionId the session ID which to associated with the user.
     * @return whether or not the session was successfully associated with the
     * user and serial number.
     */
    public boolean newSession(String username, String sessionId) throws DatabaseException {
        try {
            if (this.query(String.format("SELECT Username from Sessions WHERE Username = '%s';", username)).next())
                throw new DatabaseException("USER_ALREADY_LOGGED_IN");
            this.update(String.format("INSERT INTO sessions VALUES ('%s', '%s');", username, sessionId));
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Removes the specified session from the database
     *
     * @param sessionId the string representation of the UUID of the session
     * @return whether or not the session was successfully removed
     */
    public String removeSession(String sessionId) {
        try {
            this.update(String.format("DELETE FROM sessions WHERE SessionID = '%s';", sessionId));
        } catch (SQLException e) {
            e.printStackTrace();
            return FlowServer.ERROR;
        }
        return "OK";
    }

    /**
     * Getter for the username and serial number associated with the specified
     * session ID.
     *
     * @param sessionId the id associated with the desired session
     * @return the username and serial number associated with the specified
     * session ID
     * @throws DatabaseException if the sessionId is invalid ("INVALID_SESSION_ID") or there
     *                           is an error accessing the database ({@link FlowServer#ERROR}
     *                           ).
     */
    public ResultSet getSessionInfo(String sessionId) throws DatabaseException {
        try {
            ResultSet temp = this.query(String.format("SELECT * FROM sessions WHERE SessionID = '%s';", sessionId));
            if (temp.next()) {
                // Duplicate query because SQLite doesn't support moving back in
                // data (i.e. creating non 'TYPE_FORWARD_ONLY' ResultSets)
                return this.query(String.format("SELECT * FROM sessions WHERE SessionID = '%s';", sessionId));
            }
            throw new DatabaseException("INVALID_SESSION_ID");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new DatabaseException(FlowServer.ERROR);
    }

    /**
     * Add users to the database with the specified username and password.
     *
     * @param username the desired username
     * @param password the <b>encrypted</b> password <br>
     *                 Please don't enter passwords in plain text.
     * @return whether or not the insertion into the database was successful. It
     * could have been unsuccesful (returned false) because:<br>
     * <ul>
     * <li>The selected username already exists in the database.</li>
     * <li>An error was thrown when searching the database for all
     * current users.</li>
     * <li>An error was thrown when inserting user into the database.
     * </li>
     * </ul>
     */
    public String addUser(String username, String password) {
        try {
            // Checks if a user with the specified username already exsists
            if (this.query(String.format("SELECT username FROM users WHERE Username = '%s';", username)).next()) {
                return "USERNAME_TAKEN";
            }
        } catch (SQLException e) {
            LOGGER.severe("Error querying database from all users");
            e.printStackTrace();
            return FlowServer.ERROR;
        }

        try {
            this.update(String.format("INSERT INTO users VALUES ('%s', '%s');", username, password));
        } catch (SQLException e) {
            LOGGER.severe("Error inserting user into database");
            e.printStackTrace();
            return FlowServer.ERROR;
        }
        return "OK";
    }

    /**
     * Retrieves all associated data with the specified file.
     *
     * @param fileId the UUID of the file to retrieve.
     * @return all associated data from the 'documents' SQL table.
     * @throws DatabaseException if the file doesn't exists in the database.
     */
    public ResultSet getFileInfo(String fileId) throws DatabaseException {
        try {
            ResultSet temp = this.query(String.format("SELECT * from documents WHERE DocumentID = '%s';", fileId));
            if (temp.next()) {
                return temp;
            } else {
                // Throw an exception in this case because the server expects to
                // use the found file, this prevents a '!=null' check
                throw new DatabaseException("INVALID_FILE_UUID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Throw an exception in this case because the server expects to use
        // the found file, this prevents a '!=null' check
        throw new DatabaseException(FlowServer.ERROR);
    }

    /**
     * Retrieves all associated info with the specified ProjectID.
     *
     * @param projectUUID the {@link UUID#toString toString} of the UUID of the project.
     * @return all associated information from the projects table.
     * @throws DatabaseException if there is an error accessing the database.
     */
    public ResultSet getProjectInfo(String projectUUID) throws DatabaseException {
        try {
            ResultSet info = this.query(String.format("SELECT * FROM projects WHERE ProjectID = '%s';", projectUUID));
            if (info.next())
                return info;
            throw new DatabaseException("PROJECT_NOT_FOUND");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException(FlowServer.ERROR);
        }
    }

    /**
     * Retrieves all associated info with the specified ProjectID.
     *
     * @param directoryId the {@link UUID#toString toString} of the UUID of the
     *                    directory.
     * @return all associated information from the directories table.
     * @throws DatabaseException if there is an error accessing the database.
     */
    public ResultSet getDirectoryInfo(String directoryId) throws DatabaseException {
        try {
            ResultSet temp = this.query(String.format("SELECT * FROM directories WHERE DirectoryID = '%s';", directoryId));
            if (temp.next())
                return temp;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new DatabaseException(FlowServer.ERROR);
    }

    /**
     * Renames the specified project. This only changes the <b>given</b> project
     * name, <b>not the UUID</b>, hence not the internal server directory
     * structure either.
     *
     * @param projectUUID the UUID of the project to rename.
     * @param newName     the name which to assign to the project.
     */
    public String renameProject(String projectUUID, String newName) {
        try {
            if (!this.query(String.format("SELECT * from projects WHERE ProjectID = '%s';", projectUUID)).next()) {
                return "INVALID_PROJECT_UUID";
            }
            this.update(String.format("UPDATE projects SET ProjectName = '%s' WHERE ProjectID = '%s';", newName, projectUUID));

            this.update(String.format("UPDATE Directories SET DirectoryName = '%s' WHERE DirectoryID = '%s';", newName, projectUUID));
        } catch (SQLException e) {
            e.printStackTrace();
            return FlowServer.ERROR;
        }
        return "OK";
    }

    /**
     * Associated a new name with the specified directory.
     *
     * @param directoryUUID the string representation of the UUID of the directory.
     * @param newName       the new name which to associated with the directory.
     * @return the status of the operation, either 'INVALID_DIRECTORY_UUID' or
     * {@link FlowServer#ERROR} .
     */
    public String renameDirectory(String directoryUUID, String newName) {
        try {
            if (!this.query(String.format("SELECT * from directories WHERE DirectoryID = '%s';", directoryUUID)).next()) {
                return "INVALID_DIRECTORY_UUID";
            }
            if (this.query(String.format("SELECT * FROM Directories WHERE ParentDirectoryID = (SELECT ParentDirectoryID FROM Directories WHERE DirectoryID = '%s') AND DirectoryName = '%s';", directoryUUID, newName)).next()) {
                return "DIRECTORY_NAME_INVALID";
            }
            this.update(String.format("UPDATE Directories SET DirectoryName = '%s' WHERE DirectoryID = '%s';", newName, directoryUUID));
        } catch (SQLException e) {
            e.printStackTrace();
            return FlowServer.ERROR;
        }
        return "OK";
    }

    /**
     * Associates a new name with the specified file.
     *
     * @param fileUUID the string representation of the UUID of the file to rename.
     * @param newName  the new name to associated with the specified file.
     * @return the status of the operation, either 'OK', 'INVALID_FILE_UUID' (if
     * the file is not found) or {@link FlowServer#ERROR}.
     */
    public String renameFile(String fileUUID, String newName) {
        try {
            if (!this.query(String.format("SELECT * from Documents WHERE DocumentID = '%s';", fileUUID)).next()) {
                return "INVALID_FILE_UUID";
            }
            if (this.query(String.format("SELECT * FROM Documents WHERE ParentDirectoryID = (SELECT ParentDirectoryID FROM Documents WHERE DocumentID = '%s') AND DocumentName = '%s';", fileUUID, newName)).next()) {
                return "FILE_NAME_INVALID";
            }
            this.update(String.format("UPDATE Documents SET DocumentName = '%s' WHERE DocumentID = '%s';", newName, fileUUID));
        } catch (SQLException e) {
            e.printStackTrace();
            return FlowServer.ERROR;
        }
        return "OK";
    }

    /**
     * Deletes the specified project from the database. This will
     * <b>permanently</b> delete the project and access to all contained files
     * for <b>all users</b>. <br>
     * <br>
     * This method should only be called after verifying that the current
     * session belongs to the user which is the <b>owner</b> of the specified
     * project.
     *
     * @param projectId the string representation of the UUID of the project to
     *                  delete.
     */
    public String deleteProject(String projectId) {
        try {
            if (!this.query(String.format("SELECT * from projects WHERE ProjectID = '%s';", projectId)).next()) {
                return "INVALID_PROJECT_UUID";
            }
            this.update(String.format("DELETE FROM projects WHERE ProjectID = '%s';", projectId));
            this.update(String.format("DELETE FROM access WHERE ProjectID = '%s';", projectId));
            this.update(String.format("DELETE FROM documents WHERE ProjectID = '%s';", projectId));
            this.update(String.format("DELETE FROM directories WHERE ProjectID = '%s';", projectId));
        } catch (SQLException e) {
            e.printStackTrace();
            return FlowServer.ERROR;
        }
        return "OK";
    }

    /**
     * Deletes directory and all sub directories and files.
     *
     * @param directoryUUID the string representation of the UUID of the directory to
     *                      delete.
     * @return the status of the deletion.
     */
    public String deleteDirectory(String directoryUUID) {
        try {
            if (!this.query(String.format("SELECT * from Directories WHERE DirectoryID = '%s';", directoryUUID)).next()) {
                return "INVALID_DIRECTORY_UUID";
            }
            this.update(String.format("DELETE FROM Directories WHERE DirectoryID = '%s';", directoryUUID));
            this.update(String.format("DELETE FROM Documents WHERE ParentDirectoryID = '%s';", directoryUUID));
            try {
                ResultSet subDirectories = this.getDirectoriesInDirectory(directoryUUID);

                // Recursively delete all sub directories and files
                while (subDirectories.next()) {
                    this.deleteDirectory(subDirectories.getString("DirectoryID"));
                }
            } catch (DatabaseException e) {
                e.printStackTrace();
                return e.getMessage();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return FlowServer.ERROR;
        }
        return "OK";
    }

    /**
     * Deletes the specified file from the database.
     *
     * @param fileUUID the string representation of the UUID of the file to delete.
     * @return the status of the deletion, either 'OK', 'INVALID_FILE_UUID' (if
     * the file is not found) or {@link FlowServer#ERROR}.
     */
    public String deleteFile(String fileUUID) {
        try {
            if (!this.query(String.format("SELECT * from Documents WHERE DocumentID = '%s';", fileUUID)).next()) {
                return "INVALID_FILE_UUID";
            }
            this.update(String.format("DELETE FROM Documents WHERE DocumentID = '%s';", fileUUID));

            this.update(String.format("DELETE FROM Versions WHERE DocumentID = '%s';", fileUUID));
        } catch (SQLException e) {
            e.printStackTrace();
            return FlowServer.ERROR;
        }
        return "OK";
    }

    /**
     * Allows a user to remove their account and <b>all projects which they are
     * the owner</b> from the database (this includes all the documents of those
     * projects). This means these projects will <b>no longer be accessible</b>.
     * Please change the owner of any projects that other users wish to continue
     * developing.
     *
     * @param username the username associated with the account to close.
     */
    public String closeAccount(String username) {
        try {
            if (!this.query(String.format("SELECT * from users WHERE Username = '%s';", username)).next()) {
                return "USERNAME_DOES_NOT_EXIST";
            }

            // Delete all associated information and projects which they are the
            // owner of (not projects which they only have edit or view access
            // to)
            this.update(String.format("DELETE FROM Documents WHERE ProjectID IN (SELECT ProjectID FROM Projects WHERE OwnerUsername = '%s');", username));
            this.update(String.format("DELETE FROM users WHERE Username = '%s';", username));
            this.update(String.format("DELETE FROM projects WHERE OwnerUsername = '%s';", username));
            this.update(String.format("DELETE FROM sessions WHERE Username = '%s';", username));
            this.update(String.format("DELETE FROM access WHERE Username = '%s';", username));

        } catch (SQLException e) {
            e.printStackTrace();
            return FlowServer.ERROR;
        }
        return "OK";
    }

    /**
     * Allows users to change their password.
     *
     * @param username    the username of the user (unique)
     * @param newPassword the new password which the user wants to associate with their
     *                    username
     */
    public String changePassword(String username, String newPassword) {
        try {
            if (!this.query(String.format("SELECT * from users WHERE Username = '%s';", username)).next()) {
                return "USERNAME_DOES_NOT_EXIST";
            }
            this.update(String.format("UPDATE users SET Password = '%s' WHERE Username = '%s';", newPassword, username));
        } catch (SQLException e) {
            e.printStackTrace();
            return FlowServer.ERROR;
        }
        return "OK";
    }

    /**
     * Internal method which calls the  method
     * with the specified query.
     *
     * @param query the SQL statement to search the database with.
     * @return the results returned from the server.
     */
    ResultSet query(String query) throws SQLException {
        Statement statement = this.connection.createStatement();
        statement.setQueryTimeout(TIMEOUT);
        return statement.executeQuery(query);
    }

    /**
     * Internal method which calls the {@link Statement#executeUpdate} method
     * with the specified query.
     *
     * @param query the SQL statement to update the database with.
     */
    void update(String query) throws SQLException {
        Statement statement = this.connection.createStatement();
        statement.setQueryTimeout(TIMEOUT);
        statement.executeUpdate(query);
    }

    /**
     * Checks if the specified username exists in the database.
     *
     * @param username the user's username (unique)
     * @return whether or not the username exists in the database.
     * @throws DatabaseException if there is an error accessing the database.
     */
    public boolean userExists(String username) throws DatabaseException {
        try {
            return this.query("SELECT Username FROM Users WHERE Username = '" + username + "';").next();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException(FlowServer.ERROR);
        }
    }

    /**
     * Retrieves the username associated with the specified session ID.
     *
     * @param sessionID the UUID, in string form, of the session to search for.
     * @return the username associated with the specified session ID.
     * @throws DatabaseException if there is an error accessing the database or the session
     *                           doesn't exist.
     */
    public String getUsername(String sessionID) throws DatabaseException {
        try {
            ResultSet data = this.getSessionInfo(sessionID);
            if (!data.next())
                throw new DatabaseException("INVALID_SESSION_ID");
            return data.getString("Username");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
    }

    /**
     * Verifies if the user associated with the specified session ID has at
     * least VIEW access to the specified project.<br>
     * <br>
     * *NOTE: this method is more efficient than, but yields the equivalent
     * result of calling
     *
     * and using {@link SQLDatabase#VIEW} for the accessLevel.
     *
     * @param sessionID   the UUID of the session, in String form.
     * @param projectUUID the UUID of the project, in String form.
     * @return whether or not the user has at least VIEW access to the specified
     * project.
     * @throws DatabaseException if there is an error accessing the database.
     */
    public boolean verifyPermissions(String sessionID, String projectUUID) throws DatabaseException {
        try {
            return this.query(String.format("SELECT * FROM access WHERE Username = '%s' AND ProjectID = '%s';", this.getUsername(sessionID), projectUUID)).next();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException(FlowServer.ERROR);
        }
    }

    /**
     * Verifies if the user associated with the specified session ID has at
     * least VIEW access to the specified project.
     *
     * @param sessionID   the UUID of the session, in String form.
     * @param projectUUID the UUID of the project, in String form.
     * @param accessLevel the minimum level of access required to perform the action.
     * @return whether or not the user has at least the specified access level
     * to the specified project.
     * @throws DatabaseException if there is an error accessing the database.
     */
    public boolean verifyPermissions(String sessionID, String projectUUID, int accessLevel) throws DatabaseException {
        try {
            return this.query(String.format("SELECT * FROM access WHERE Username = '%s' AND ProjectID = '%s' AND AccessLevel > '%d';", this.getUsername(sessionID), projectUUID, accessLevel - 1)).next();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException(FlowServer.ERROR);
        }
    }

    /**
     * Get all users with the specified access level to the specified project.
     *
     * @param projectUUID the UUID, in String form, of the project.
     * @param accessLevel the access level, defined in the access constants (
     *                    {@link OWNER}, {@link VIEW}, {@link EDITOR}).
     * @return the usernames of the users which meet the specified criteria.
     * @throws DatabaseException
     */
    public String[] getUsers(String projectUUID, int accessLevel) throws DatabaseException {
        try {
            return Results.toStringArray("Username", this.query(String.format("SELECT Username FROM Access WHERE ProjectID = '%s' AND AccessLevel = '%d';", projectUUID, accessLevel)));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException(FlowServer.ERROR);
        }
    }

    /**
     * Get all available versions of the specified file.
     *
     * @param fileUUID the String representation of the UUID of the file.
     * @return array containing the string representations of the UUIDs of the
     * versions.
     * @throws DatabaseException if there is an error accessing the database.
     */
    public String[] getFileVersions(String fileUUID) throws DatabaseException {
        try {
            ResultSet response = this.query(String.format("SELECT VersionID FROM Versions WHERE DocumentID = '%s'", fileUUID));
            return Results.toStringArray("VersionID", response);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException(FlowServer.ERROR);
        }
    }

    /**
     * Retrieve all associated information with the specified version UUID.
     *
     * @param versionUUID the String representation of the UUID of the version.
     * @return All associated information from columns: 'VersionID', 'Date', and
     * 'DocumentID'
     * @throws DatabaseException if there is an error accessing the database or the version
     *                           doesn't exist.
     */
    private ResultSet getVersionInfo(String versionUUID) throws DatabaseException {
        try {
            ResultSet response = this.query(String.format("SELECT * FROM Versions WHERE VersionID = '%s';", versionUUID));
            if (response.next())
                return response;
            throw new DatabaseException("INVALID_VERSION_UUID");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException(FlowServer.ERROR);
        }
    }

    /**
     * Retrieve save date of the specified version.
     *
     * @param versionUUID the String representation of the UUID of the version.
     * @return the number of milliseconds since January 1, 1970, 00:00:00 GMT,
     * for when the version was created.
     * @throws DatabaseException if there is an error accessing the database or the version
     *                           doesn't exist.
     */
    public long getVersionDate(String versionUUID) throws DatabaseException {
        ResultSet response = this.getVersionInfo(versionUUID);
        try {
            return response.getLong("Date");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException(FlowServer.ERROR);
        }
    }

    /**
     * Easy retrieval of file type given the UUID.
     *
     * @param fileUUID the String representation of the UUID of the file.
     * @return the type of the file, either {@link ARBITRARY_DOCUMENT} or
     * {@link TEXT_DOCUMENT}.
     * @throws DatabaseException if there is an error accessing the database or the file
     *                           doesn't exist.
     */
    public String getFileType(String fileUUID) throws DatabaseException {
        ResultSet response = this.getFileInfo(fileUUID);
        try {
            return response.getString("FileType");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException(FlowServer.ERROR);
        }
    }

    /**
     * Retrieves UUID of the latest version of the specified document.
     *
     * @param fileUUID the string representation of the UUID of the file.
     * @return the string representation of the UUID of the latest version of
     * the specified file.
     * @throws DatabaseException if there is an error accessing the database or the file
     *                           doesn't exist in the database.
     */
    public String getLatestVersionUUID(String fileUUID) throws DatabaseException {
        ResultSet response;
        try {
            response = this.query(String.format("SELECT VersionID from Versions WHERE Date IN (SELECT MAX(Date) FROM Versions WHERE DocumentID = '%s');", fileUUID));
            if (response.next())
                return response.getString("VersionID");
            throw new DatabaseException("INVALID_FILE_UUID");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException(FlowServer.ERROR);
        }
    }

    /**
     * Get the string representation of the UUID of the project associated with
     * the specified directory.
     *
     * @param directoryUUID the string representation of the UUID of the directory.
     * @return the string representation of the UUID of the project.
     * @throws DatabaseException if the directory UUID is invalid, or there is an error
     *                           accessing the database.
     */
    public String getProjectUUIDFromDirectory(String directoryUUID) throws DatabaseException {
        try {
            ResultSet response = this.query(String.format("SELECT ProjectID FROM Directories WHERE DirectoryID = '%s';", directoryUUID));
            if (response.next()) {
                return response.getString("ProjectID");
            }
            throw new DatabaseException("INVALID_DIRECTORY_UUID");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException(FlowServer.ERROR);
        }
    }

    /**
     * Get the string representation of the UUID of the project associated with
     * the specified file.
     *
     * @param fileUUID the string representation of the UUID of the file.
     * @return the string representation of the UUID of the project.
     * @throws DatabaseException if the file UUID is invalid, or there is an error accessing
     *                           the database.
     */
    public String getProjectUUIDFromFile(String fileUUID) throws DatabaseException {
        try {
            ResultSet response = this.query(String.format("SELECT ProjectID FROM Documents WHERE DocumentID = '%s';", fileUUID));
            if (response.next()) {
                return response.getString("ProjectID");
            }
            throw new DatabaseException("INVALID_FILE_UUID");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException(FlowServer.ERROR);
        }
    }

    /**
     * Get the string representation of the UUID of the project associated with
     * the specified version.
     *
     * @param versionUUID the string representation of the UUID of the version.
     * @return the string representation of the UUID of the project.
     * @throws DatabaseException if the version UUID is invalid, or there is an error
     *                           accessing the database.
     */
    public String getProjectUUIDFromVersion(String versionUUID) throws DatabaseException {
        try {
            ResultSet response = this.query(String.format("SELECT ProjectID FROM Documents WHERE DocumentID IN (SELECT DocumentID FROM Versions WHERE VersionID = '%s');", versionUUID));
            if (response.next()) {
                return response.getString("ProjectID");
            }
            throw new DatabaseException("INVALID_VERSION_UUID");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException(FlowServer.ERROR);
        }
    }

    /**
     * Check for any corruption in the specified database file using the backup
     * database as a look up.
     *
     * @return whether or not any corruption was detected in the database. This
     * method will return false if <b>any</b> table, table column name,
     * or the table count is different than the backup database. Also,
     * this method will catch any exceptions thrown when accessing the
     * database and return false, as it signifies that the database is
     * corrupted.
     */
    private boolean checkForDatabaseCorruption(String backUpDatabase) {
        // Establish a connection to the backup database
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + SQLDatabase.BACKUP_DATABASE);
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.warning("Error connecting to database located at: " + SQLDatabase.BACKUP_DATABASE);
            return false;
        }

        // Initialize the lists to carry data between the try / catch blocks
        ArrayList<String> tableNames = new ArrayList<>();
        ArrayList<ArrayList<String>> tableColumns = new ArrayList<>();
        try {
            DatabaseMetaData databaseMetaData = this.connection.getMetaData();
            ResultSet tables = databaseMetaData.getTables(null, null, "%", null);
            while (tables.next()) {
                tableNames.add(tables.getString("TABLE_NAME"));
            }
            for (String table : tableNames) {
                ResultSet tableInfo = databaseMetaData.getColumns(null, null, table, null);
                ArrayList<String> columnInfo = new ArrayList<>();
                while (tableInfo.next()) {
                    columnInfo.add(tableInfo.getString("COLUMN_NAME"));
                }
                tableColumns.add(columnInfo);
            }
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.warning("Error loading database back up meta data: " + SQLDatabase.LIVE_DATABASE);
            return false;
        }
        // Establish a connection to the live database
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + SQLDatabase.LIVE_DATABASE);
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.warning("Error connecting to database located at: " + SQLDatabase.LIVE_DATABASE);
            return false;
        }

        try {
            DatabaseMetaData databaseMetaData = this.connection.getMetaData();
            for (int i = 0; i < tableNames.size(); i++) {
                // Test query for table corruption check
                // SQLException will be thrown if corrupted
                this.query(String.format("SELECT * FROM '%s';", tableNames.get(i)));

                // Check if column names in each table match with back up
                ResultSet tableInfo = databaseMetaData.getColumns(null, null, tableNames.get(i), null);
                ArrayList<String> columnInfo = tableColumns.get(i);
                for (String aColumnInfo : columnInfo) {
                    tableInfo.next();
                    if (!aColumnInfo.equals(tableInfo.getString("COLUMN_NAME")))
                        return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.warning("Error accessing database: " + SQLDatabase.LIVE_DATABASE);
            return false;
        }
        return true;
    }

    /**
     * Wipes and restores the specified file system with the provided backup.
     *
     * @param backUpFileSystem the path to the folder containing the backup which will be
     *                         copied into the corruptFileSystem path.
     * @throws IOException
     */
    private void recoverFileSystem(String backUpFileSystem) throws IOException {
        // Close the connection before starting the procedure
        try {
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Delete all data in the corrupt file system path
        this.deleteFileSystemDirectory(new File(SQLDatabase.LIVE_FOLDER));

        // Copy over all directories and files from the back up folder to the
        // specified path
        try {
            this.copyFileSystem(new File(BACKUP_FOLDER), new File(LIVE_FOLDER));
            LOGGER.warning("Database recovery successful!");
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.severe("Error recovering the database and file system from backup, restarting recovery proccess...");

            // Is an exception is thrown, restart the restoration process one
            // more time for a final try at recovery.
            try {
                this.deleteFileSystemDirectory(new File(SQLDatabase.LIVE_FOLDER));
                this.copyFileSystem(new File(BACKUP_FOLDER), new File(LIVE_FOLDER));
            } catch (Exception e1) {
                // If an exception is thrown again, likely even the backup files
                // and corrupted / inaccessible / unusable. This means that
                // recovery is unfortunately not possible.
                e1.printStackTrace();
                LOGGER.severe("Error recovering the database and file system from backup, shutting down the server...\n Please contact your system administrator or visit https://github.com/ecnivo/Flow/releases for a clean filesystem.");
                System.exit(0);
            }
        }

        // Reconnects to the database
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + LIVE_DATABASE);
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.warning("Error connecting to database located at: " + LIVE_DATABASE);
        }
    }

    /**
     * Recursively deletes the specified file / directory and all of its
     * sub-directories files.
     *
     * @param file the file or directory to delete.
     * @throws IOException
     */
    private void deleteFileSystemDirectory(File file) throws IOException {
        LOGGER.warning("Deleting all user files and documents...");
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File tempFile : contents) {
                deleteFileSystemDirectory(tempFile);
            }
        }
        if (!file.delete()) {
            throw new IOException();
        }
    }

    /**
     * Recursively copies all data from a backup folder to the specified folder.
     *
     * @param backup the folder to copy data from.
     * @param live   the path to copy data to.
     * @throws IOException if there is an error finding or reading from the files /
     *                     directories.
     */
    private void copyFileSystem(File backup, File live) throws IOException {
        LOGGER.warning("Restoring data from back up...");
        if (backup.isDirectory()) {
            // Create the folder at the specified path if it doesn't exist
            if (!live.exists())
                live.mkdirs();

            // Recursively copy over all sub-directories and files
            String[] files = backup.list();
            for (String file : files) {
                LOGGER.info(file);
                copyFileSystem(new File(backup, file), new File(live, file));
            }
        } else {
            // Copy the file data
            FileInputStream in = new FileInputStream(backup);
            LOGGER.info(live.toString());
            FileOutputStream out = new FileOutputStream(live);
            byte[] buffer = new byte[1024];
            int length = in.read(buffer);
            while (length > 0) {
                out.write(buffer, 0, length);
                length = in.read(buffer);
            }
            in.close();
            out.close();
        }
    }

    /**
     * Deletes all data in the session table to log out all previously logged in
     * users.
     *
     * @throws SQLException if there is an error accessing the database.
     */
    private void refreshSessions() throws SQLException {
        this.update("DELETE FROM Sessions;");
    }

    /**
     * Scans through all documents and user accounts, deletes all corrupted
     * documents / users.
     *
     * @param dataFolder the folder which contains all of the data (i.e. the user and
     *                   files folders).
     * @return Whether or not the corruption fixing process was successful. Only
     * returns false if the corruption was not able to be fixed by
     * removing select users / documents. otherwise, returns true.
     */
    private boolean checkAndRepairFileSystemCorruption(String dataFolder) {
        try {
            LOGGER.warning("Scanning for database and file system corruption...");
            // Scans users for missing files and removes associates accounts
            ResultSet response = this.query("SELECT Username FROM Users;");
            while (response.next()) {
                String username = response.getString("Username");
                if (!DataManagement.getInstance().userExists(username)) {
                    LOGGER.warning("Deleting account '" + username + "' due to corruption.");
                    this.closeAccount(username);
                }
            }

            // Scans all documents for missing data and removes them
            response = this.query("SELECT DocumentID FROM Documents;");
            while (response.next()) {
                String fileUUID = response.getString("DocumentID");
                if (!DataManagement.getInstance().fileExists(UUID.fromString(fileUUID))) {
                    LOGGER.warning("Deleting file '" + fileUUID + "' due to corruption.");
                    this.deleteFile(fileUUID);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // The repair process was not successful, thus must wipe database
            // and entire file-system to ensure integrity of server.
            try {
                this.connection.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
                return false;
            }
            return false;
        }
        return true;
    }
}
