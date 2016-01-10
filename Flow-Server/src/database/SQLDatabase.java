package database;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import server.FlowServer;
import util.DatabaseException;
import util.Results;

public class SQLDatabase {

	/**
	 * Location where the SQLite JDBC drivers are stored
	 */
	public static final String DRIVER = "org.sqlite.JDBC";

	// TODO Properly implement the timeout
	/**
	 * Number of seconds to allow for searching before timeout
	 */
	public static final int TIMEOUT = 5;

	public static final int ADD_USER = 0, REMOVE_USER = -1, NONE = 0, VIEW = 1,
			EDIT = 2, OWNER = 3;

	/**
	 * Connection to the database.
	 */
	private Connection connection;

	public SQLDatabase(String databaseName) {
		try {
			DriverManager.registerDriver(
					(Driver) Class.forName(DRIVER).newInstance());
		} catch (Exception e) {
			System.out
					.println("Error loading database driver: " + e.toString());
			return;
		}

		try {
			this.connection = DriverManager
					.getConnection("jdbc:sqlite:" + databaseName);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(
					"Error connecting to database located at: " + databaseName);
		}
	}

	/**
	 * Getter for all projects associated with the specified username,
	 * completely ignoring whether the user is the owner, or has only edit or
	 * view access.
	 *
	 * @param username
	 *            the ID of the user.
	 * @return all projects associated with the specified username.
	 * @throws DatabaseException
	 *             if there is an error accessing the database.
	 */
	public ResultSet getProjects(String username) throws DatabaseException {
		try {
			// Note: deleted the checking code, since a user can have no
			// projects, which would throw an error, checking code moved to
			// external method
			return this.query(String.format(
					"SELECT * FROM access WHERE Username = '%s';", username));
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(FlowServer.ERROR);
		}
	}

	/**
	 * Specifies the access level to a specific project for a user.
	 *
	 * @param accessLevel
	 *            the level of access provided to the user, either {@link OWNER}
	 *            , {@link EDIT}, or {@link VIEW}.
	 * @param projectId
	 *            the project which to provide the user access to.
	 * @param username
	 *            the username which to provide access to.
	 * @return whether or not the access was successfully granted.
	 */
	public String updateAccess(int accessLevel, String projectId,
			String username) {
		// TODO Implement check for if username exists
		try {
			if (accessLevel == EDIT || accessLevel == VIEW) {
				this.update(String.format(
						"INSERT INTO access values('%s', '%s', '%s');",
						projectId, username, accessLevel));
			} else if (accessLevel == OWNER) {
				// Changes the owner of the project in the projects table
				this.update(String.format(
						"UPDATE projects SET OwnerUsername = '%s' WHERE ProjectID = '%s';",
						username, projectId));

				this.update(String.format(
						"DELETE FROM access WHERE Username = '%s' AND ProjectID = '%s';",
						username, projectId));

				// Changes the permissions of the user to be an owner
				this.update("INSERT INTO access values('" + projectId + "', '"
						+ username + "', '" + OWNER + "');");
			} else if (accessLevel == NONE) {
				this.update(String.format(
						"DELETE FROM access WHERE Username = '%s' AND ProjectID = '%s';",
						username, projectId));
			} else {
				return "ACCESS_LEVEL_INVALID";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return FlowServer.ERROR;
		}
		return "OK";
	}

	/**
	 * Getter for all files associated with the specified project.
	 *
	 * @param projectId
	 *            the ID of the project.
	 * @return all associated files.
	 */
	public ResultSet getFiles(String projectId) throws DatabaseException {
		try {
			// TODO Add check if for project is exists
			return this.query(String.format(
					"SELECT * FROM documents WHERE ProjectID = '%s';",
					projectId));
		} catch (SQLException e) {
			e.printStackTrace();
			// TODO: this won't be called for the above reason, move this
			// exception to the check (Above)
			throw new DatabaseException("PROJECT_DOES_NOT_EXIST");
		}
		// return null;
	}

	/**
	 * Creates a new project with the specified name and owner
	 *
	 * @param projectName
	 *            name of the project
	 * @param ownerId
	 *            ID of the user who creates the project
	 */
	public String newProject(String projectId, String projectName,
			String ownerId) {
		try {
			// TODO Add check to make sure user doesn't have two projects with
			// same name
			this.update(String.format(
					"INSERT INTO projects VALUES('%s', '%s', '%s');", projectId,
					projectName, ownerId));
		} catch (SQLException e) {
			e.printStackTrace();
			return FlowServer.ERROR;
		}
		return this.newDirectory(projectName, projectId, projectId, projectId);
	}

	/**
	 * Creates a new file within the specified project.
	 *
	 * @param fileId
	 *            the ID of the file ({@link UUID#toString() string
	 *            representation} of the UUID associated with file).
	 * @param fileName
	 *            the name of the file (including the extension).
	 * @param projectId
	 *            the ID of the project which to place the file inside
	 * @param directoryId
	 *            the ID of the directory which to place the file inside
	 */
	public String newFile(String fileId, String fileName, String projectId,
			String directoryId) {
		try {
			this.update(String.format(
					"INSERT INTO documents VALUES('%s', '%s', '%s', '%s');",
					fileId, projectId, fileName, directoryId));
		} catch (SQLException e) {
			e.printStackTrace();
			return FlowServer.ERROR;
		}
		return "OK";
	}

	/**
	 * Creates a new file within the specified project.
	 *
	 * @param directoryName
	 *            the name of the directory.
	 * @param directoryId
	 *            the ID of the directory ({@link UUID#toString() string
	 *            representation} of the UUID associated with the directory).
	 * @param projectId
	 *            the ID of the project which to place the directory inside.
	 * @param parentDirectoryId
	 *            the ID of the directory which to place the directory inside.
	 */
	public String newDirectory(String directoryName, String directoryId,
			String projectId, String parentDirectoryId) {
		try {
			// TODO Add checks
			this.update(String.format(
					"INSERT INTO directories VALUES('%s', '%s', '%s', '%s');",
					directoryId, parentDirectoryId, directoryName, projectId));
		} catch (SQLException e) {
			e.printStackTrace();
			return FlowServer.ERROR;
		}
		return "OK";
	}

	/**
	 * Getter for all of the usernames in the database.
	 *
	 * @return all of the usernames in the database.
	 * @throws DatabaseException
	 */
	public ResultSet getUserNames(String projectId) throws DatabaseException {
		try {
			if (!this.query(String.format(
					"SELECT * from projects WHERE ProjectID = '%s';",
					projectId)).next()) {
				throw new DatabaseException("PROJECT_DOES_NOT_EXIST");
			}
			return this.query(String.format(
					"SELECT Username FROM access WHERE ProjectID = '%s';",
					projectId));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		throw new DatabaseException(FlowServer.ERROR);
	}

	/**
	 * Authenticates a user by verifying if the specified username and password
	 * pair exists in the database.
	 *
	 * @param username
	 *            the user's username.
	 * @param password
	 *            the user's encrypted password.
	 * @return whether or not the username and password exists in the database.
	 */
	public boolean authenticate(String username, String password) {
		try {
			ResultSet pair = this.query(String.format(
					"SELECT Password FROM users WHERE Username = '%s';",
					username));
			if (pair.next()) {
				// TODO Verify
				return password.equals(pair.getString("password"));
			}
		} catch (SQLException e) {
			// TODO Remove this debugging message
			System.err.println("Error authenticating user: " + username
					+ " with password: " + password);
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Creates a new session for the specified username and serial number.
	 *
	 * @param username
	 *            the UUID of the user.
	 * @param serialNumber
	 *            the serial number of the user's hard drive.
	 * @param sessionId
	 *            the session ID which to associated with the user.
	 * @return whether or not the session was successfully associated with the
	 *         user and serial number.
	 */
	public boolean newSession(String username, String sessionId) {
		try {
			this.update(String.format(
					"INSERT INTO sessions VALUES ('%s', '%s');" , username,
					sessionId));
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Removes the specified session from the database
	 *
	 * @param sessionId
	 *            the string representation of the UUID of the session
	 * @return whether or not the session was successfully removed
	 */
	public boolean removeSession(String sessionId) {
		try {
			// TODO Verify if the session actually exists prior to removal
			this.update(String.format(
					"DELETE FROM sessions WHERE SessionID = '%s';", sessionId));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Getter for the username and serial number associated with the specified
	 * session ID.
	 *
	 * @param sessionId
	 *            the id associated with the desired session
	 * @return the username and serial number associated with the specified
	 *         session ID
	 * @throws DatabaseException
	 *             if the sessionId is invalid ("INVALID_SESSION_ID") or there
	 *             is an error accessing the database ({@link FlowServer#ERROR}
	 *             ).
	 */
	public ResultSet getSessionInfo(String sessionId) throws DatabaseException {
		try {
			ResultSet temp = this.query(String.format(
					"SELECT * FROM sessions WHERE SessionID = '%s';",
					sessionId));
			if (temp.next()) {
				// Duplicate query because SQLite doesn't support moving back in
				// data (i.e. creating non 'TYPE_FORWARD_ONLY' ResultSets)
				return this.query(String.format(
						"SELECT * FROM sessions WHERE SessionID = '%s';",
						sessionId));
			}
			throw new DatabaseException("INVALID_SESSION_ID");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new DatabaseException(FlowServer.ERROR);
	}

	/**
	 * Add users to the database with the specified username and password.
	 *
	 * @param username
	 *            the desired username
	 * @param password
	 *            the <b>encrypted</b> password <br>
	 *            Please don't enter passwords in plain text.
	 * @return whether or not the insertion into the database was successful. It
	 *         could have been unsuccesful (returned false) because:<br>
	 *         <ul>
	 *         <li>The selected username already exists in the database.</li>
	 *         <li>An error was thrown when searching the database for all
	 *         current users.</li>
	 *         <li>An error was thrown when inserting user into the database.
	 *         </li>
	 *         </ul>
	 */
	public String addUser(String username, String password) {
		try {
			// Checks if a user with the specified username already exsists
			if (this.query(String.format(
					"SELECT username FROM users WHERE Username = '%s';",
					username)).next()) {
				return "USERNAME_TAKEN";
			}
		} catch (SQLException e) {
			System.err.println("Error querying database from all users");
			e.printStackTrace();
			return FlowServer.ERROR;
		}

		try {
			this.update(String.format(
					"INSERT INTO users VALUES ('%s', '%s');", username,
					password));
		} catch (SQLException e) {
			System.err.println("Error inserting user into database");
			e.printStackTrace();
			return FlowServer.ERROR;
		}
		return "OK";
	}

	/**
	 * Retrieves all associated data with the specified file.
	 *
	 * @param fileId
	 *            the UUID of the file to retrieve.
	 * @return all associated data from the 'documents' SQL table.
	 * @throws DatabaseException
	 *             if the file doesn't exists in the database.
	 */
	public ResultSet getFile(String fileId) throws DatabaseException {
		try {
			ResultSet temp;
			if ((temp = this.query(String.format(
					"SELECT * from documents WHERE DocumentID = '%s';",
					fileId))).next()) {
				temp.previous();
				return temp;
			} else {
				// Throw an exception in this case because the server expects to
				// use the found file, this prevents a '!=null' check
				throw new DatabaseException("DOCUMENT_NAME_INVALID");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch blocks
			e.printStackTrace();
		}
		// Throw an exception in this case because the server expects to use
		// the found file, this prevents a '!=null' check
		throw new DatabaseException(FlowServer.ERROR);
	}

	/**
	 * Renames the specified project. This only changes the <b>given</b> project
	 * name, <b>not the UUID</b>, hence not the internal server directory
	 * structure either.
	 *
	 * @param projectId
	 *            the UUID of the project to rename.
	 * @param newName
	 *            the name which to assign to the project.
	 * @throws DatabaseException
	 *             if the specified project UUID doesn't exists in the database
	 *             or the new name contains invalid characters.
	 */
	public String renameProject(String projectId, String newName) {
		// TODO Check if name is valid
		try {
			if (!this.query(String.format(
					"SELECT * from projects WHERE ProjectID = '%s';",
					projectId)).next()) {
				return "PROJECT_NAME_INVALID";
			}
			this.update(String.format(
					"UPDATE projects SET ProjectName = '%s' WHERE ProjectID = '%s';",
					newName, projectId));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
	 * @param projectId
	 *            the UUID of the project to delete.
	 * @throws DatabaseException
	 *             if the project doesn't exist.
	 */
	public String deleteProject(String projectId) {
		try {
			if (!this.query(String.format(
					"SELECT * from projects WHERE ProjectID = '%s';",
					projectId)).next()) {
				return "PROJECT_DOES_NOT_EXIST";
			}
			this.update(String.format(
					"DELETE FROM projects WHERE ProjectID = '%s';", projectId));
			this.update(String.format(
					"DELETE FROM access WHERE ProjectID = '%s';", projectId));
			this.update(String.format(
					"DELETE FROM documents WHERE ProjectID = '%s';",
					projectId));
			this.update(String.format(
					"DELETE FROM directories WHERE ProjectID = '%s';",
					projectId));
		} catch (SQLException e) {
			e.printStackTrace();
			return FlowServer.ERROR;
		}
		return "OK";
	}

	/**
	 * Allows a user to remove their account and <b>all projects which they are
	 * the owner</b> from the database. This means these projects will <b>no
	 * longer be accessible</b>. Please change the owner of any projects that
	 * other users wish to continue developing.
	 *
	 * @param username
	 *            the username associated with the account to close.
	 * @throws DatabaseException
	 *             if the username does not exist in the database.
	 */
	public String closeAccount(String username) {
		try {
			if (!this.query(String.format(
					"SELECT * from users WHERE Username = '%s';", username))
					.next()) {
				return "USERNAME_DOES_NOT_EXIST";
			}

			// TODO Delete all documents using the deleted project IDs
			this.update(String.format(
					"DELETE FROM users WHERE Username = '%s';", username));
			this.update(String.format(
					"DELETE FROM projects WHERE OwnerUsername = '%s';",
					username));
			this.update(String.format(
					"DELETE FROM sessions WHERE Username '%s';", username));
			this.update(String.format(
					"DELETE FROM access WHERE Username = '%s';", username));

			// TODO Delete all the actual file data from disk

		} catch (SQLException e) {
			e.printStackTrace();
			return FlowServer.ERROR;
		}
		return "OK";
	}

	/**
	 * Allows users to change their password.
	 *
	 * @param username
	 *            the username of the user (unique)
	 * @param newPassword
	 *            the new password which the user wants to associate with their
	 *            username
	 * @throws DatabaseException
	 *             if the username does not exist in the system, or the entered
	 *             password is invalid
	 */
	public String changePassword(String username, String newPassword) {
		try {
			if (!this.query(String.format(
					"SELECT * from users WHERE Username = '%s';", username))
					.next()) {
				return "USERNAME_DOES_NOT_EXIST";
			}
			this.update(String.format(
					"UPDATE users SET Password = '%s' WHERE Username = '%s';",
					newPassword, username));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return FlowServer.ERROR;
		}
		return "OK";
	}

	public String getPath(String fileId) throws DatabaseException {
		ResultSet fileData = null;
		try {
			fileData = this.query(String.format(
					"SELECT * FROM documents WHERE FileID = '%s'", fileId));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String parentDirectoryId = null, directoryId = null;
		try {
			parentDirectoryId = fileData.getString("ParentDirectoryID");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO optimize for efficiency
		StringBuilder path = new StringBuilder();
		do {
			directoryId = parentDirectoryId;
			try {
				parentDirectoryId = Results.toStringArray("ParentDirectoryID",
						this.getDirectoryInfo(directoryId))[0];
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new DatabaseException(e.getMessage());
			}
			path.append(directoryId);
		} while (!parentDirectoryId.equals(directoryId));

		return path.toString();
	}

	/**
	 * Internal method which calls the {@link Statement#ExecuteQuery} method
	 * with the specified query.
	 *
	 * @param query
	 *            the SQL statement to search the database with.
	 * @return the results returned from the server.
	 */
	ResultSet query(String query) throws SQLException {
		Statement statement = this.connection.createStatement();
		statement.setQueryTimeout(TIMEOUT);
		return statement.executeQuery(query);
	}

	/**
	 * Internal method which calls the {@link Statement#ExecuteUpdate} method
	 * with the specified query.
	 *
	 * @param query
	 *            the SQL statement to update the database with.
	 */
	void update(String query) throws SQLException {
		Statement statement = this.connection.createStatement();
		statement.setQueryTimeout(TIMEOUT);
		statement.executeUpdate(query);
	}

	/**
	 * Checks if the specified username exists in the database.
	 *
	 * @param username
	 *            the user's username (unique)
	 * @return whether or not the username exists in the database.
	 * @throws DatabaseException
	 *             if there is an error accessing the database.
	 */
	public boolean userExists(String username) throws DatabaseException {
		try {
			return this.query(
					"SELECT * FROM users WHERE Username = '" + username + "';")
					.next();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(FlowServer.ERROR);
		}
	}

	/**
	 * Retrieves all associated info with the specified ProjectID.
	 *
	 * @param projectId
	 *            the {@link UUID#toString toString} of the UUID of the project.
	 * @return all associated information from the projects table.
	 * @throws DatabaseException
	 *             if there is an error accessing the database.
	 */
	public ResultSet getProjectInfo(String projectId) throws DatabaseException {
		try {
			return this.query(String.format(
					"SELECT * FROM projects WHERE ProjectID = '%s';",
					projectId));
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(FlowServer.ERROR);
		}
	}

	/**
	 * Retrieves all associated info with the specified ProjectID.
	 *
	 * @param directoryId
	 *            the {@link UUID#toString toString} of the UUID of the
	 *            directory.
	 * @return all associated information from the directories table.
	 * @throws DatabaseException
	 *             if there is an error accessing the database.
	 */
	public ResultSet getDirectoryInfo(String directoryId)
			throws DatabaseException {
		try {
			return this.query(String.format(
					"SELECT * FROM directories WHERE DirectoryID = '%s';",
					directoryId));
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(FlowServer.ERROR);
		}
	}
}
