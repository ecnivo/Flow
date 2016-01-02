package database;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import util.DatabaseException;

public class SQLDatabase {

	/**
	 * Location where the SQLite JDBC drivers are stored
	 */
	public static final String DRIVER = "org.sqlite.JDBC";

	// TODO Implement the timeout
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
	 * Getter for all projects associated with the specified UserID, completely
	 * ignoring whether the user is the owner, or has only edit or view access.
	 * 
	 * @param username
	 *            the ID of the user.
	 * @return all projects associated with the specified UserID.
	 */
	public ResultSet getProjects(String username) {
		try {
			return this.query(
					"SELECT * FROM access WHERE Username = " + username + ";");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
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
	public boolean updateAccess(int accessLevel, String projectId,
			String username) throws DatabaseException {
		// TODO Password project (ask username / password as parameter)
		try {
			if (accessLevel == EDIT || accessLevel == VIEW) {
				this.update("INSERT INTO access values('" + projectId + "', '"
						+ username + "', " + accessLevel + ";");
				return true;
			} else if (accessLevel == OWNER) {
				// Changes the owner of the project in the projects table
				this.update("UPDATE projects SET OwnerUserName = '" + username
						+ "' WHERE ProjectID = '" + projectId + "';");

				this.update("DELETE FROM access WHERE Username = '" + username
						+ "' AND ProjectID = '" + projectId + "';");

				// Changes the permissions of the user to be an owner
				this.update("INSERT INTO access values(" + projectId + ", "
						+ username + ", " + OWNER + ");");

				// TODO remove all other permissions (i.e. if they had edit
				// permissions)
				return true;
			} else if (accessLevel == NONE) {
				this.update("DELETE FROM access WHERE Username = '" + username
						+ "' AND ProjectID = '" + projectId + "';");
			} else {
				throw new DatabaseException("ACCESS_LEVEL_INVALID");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
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
			// TODO Add check if project is found
			return this.query("SELECT * FROM documents WHERE ProjectID = "
					+ projectId + ";");
		} catch (SQLException e) {
			e.printStackTrace();
			// TODO: this won't be called for this reason, move this exception
			// throw to the check (Above)
			throw new DatabaseException("PROJECT_DOES_NOT_EXIST");
		}
		// return null;
	}

	/**
	 * Creates a new project with the specified name and owner
	 * 
	 * @param name
	 *            name of the project
	 * @param ownerId
	 *            ID of the user who creates the project
	 */
	public void newProject(String name, String ownerId) {
		try {
			// TODO Add check to make sure user doesn't have two projects with
			// same name
			this.update("INSERT INTO projects(ProjectName, OwnerID) values("
					+ name + ", " + ownerId + ");");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new file within the specified project.
	 * 
	 * @param fileName
	 *            the name of the file (including the extension).
	 * @param projectId
	 *            the ID of the project which to place the file inside
	 * @param directoryId
	 *            the ID of the directory which to place the file inside
	 */
	public void newFile(String fileName, String projectId, String directoryId) {
		try {
			this.update("INSERT INTO documents(ProjectID, DocumentName) values("
					+ projectId + ", " + fileName + ")");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new file within the specified project.
	 * 
	 * @param fileName
	 *            the name of the file (including the extension).
	 * @param projectId
	 *            the ID of the project which to place the file inside
	 */
	public void newFile(String fileName, String projectId) {
		try {
			this.update("INSERT INTO documents(ProjectID, DocumentName) values("
					+ projectId + ", " + fileName + ")");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new file within the specified project.
	 * 
	 * @param directoryName
	 *            the name of the directory.
	 * @param projectId
	 *            the ID of the project which to place the directory inside.
	 * @param parentDirectoryId
	 *            the ID of the directory which to place the directory inside.
	 */
	public void newDirectory(String directoryName, String projectId,
			String parentDirectoryId) {
		// TODO implement
		// try {
		// this.update("INSERT INTO documents(ProjectID, DocumentName) values("
		// + projectId + ", " + fileName + ")");
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }
	}

	/**
	 * Creates a new file within the specified project.
	 * 
	 * @param directoryName
	 *            the name of the directory.
	 * @param projectId
	 *            the ID of the project which to place the directory inside.
	 */
	public void newDirectory(String directoryName, String projectId) {
		// TODO implement
		// try {
		// this.update("INSERT INTO documents(ProjectID, DocumentName) values("
		// + projectId + ", " + fileName + ")");
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }
	}

	/**
	 * Getter for all of the usernames in the database.
	 * 
	 * @return all of the usernames in the database.
	 */
	public ResultSet getUserNames(String projectId) {
		try {
			return this.query("SELECT username FROM users WHERE ;");
		} catch (SQLException e) {
			e.printStackTrace();
			// Switch this to an empty resultset if possible
			return null;
		}
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
			ResultSet pair = this
					.query("SELECT password FROM users WHERE username = "
							+ username + ";");
			if (pair.next()) {
				// TODO Verify
				return password.equals(pair.getString("password"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
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
	public boolean newSession(String username, String serialNumber,
			String sessionId) {
		try {
			this.update("INSERT INTO sessions VALUES (" + username + ", "
					+ serialNumber + ", " + sessionId + ");");
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
			this.update("DELETE FROM sessions WHERE SessionID = " + sessionId
					+ ";");
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
	 */
	public ResultSet getSessionInfo(String sessionId) {
		try {
			return this.query(
					"SELECT * FROM sessions WHERE SessionID = " + sessionId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
	public boolean addUser(String username, String password) {
		try {
			// Checks if a user with the specified username already exsists
			if (this.query("SELECT username FROM users WHERE username = "
					+ username + ";").next()) {
				return false;
			}
		} catch (SQLException e) {
			System.err.println("Error querying database from all users");
			e.printStackTrace();
			return false;
		}

		try {
			this.update("INSERT INTO users (username, password) VALUES ("
					+ username + ", " + password + ");");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Internal method which calls the '{@link Statement#ExecuteQuery} method
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
	 * Internal method which calls the '{@link Statement#ExecuteUpdate} method
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

	public ResultSet getFile(String uuid) throws DatabaseException {
		try {
			// TODO Add check if found
			return this.query(
					"SELECT * FROM documents WHERE DocumentID = " + uuid + ";");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new DatabaseException("DOCUMENT_NAME_INVALID");
			// return null;
		}
	}

	public void renameProject(String uuid, String newName)
			throws DatabaseException {
		// TODO Check if name is valid
		try {
			this.update("UPDATE projects SET ProjectName = " + newName
					+ " WHERE ProjectID = " + uuid + ";");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// throw new DatabaseException("INVALID_PROJECT_NAME" or
			// "PROJECT_DOES_NOT_EXIST"); based on the above checks
		}
	}

	public void deleteProject(String projectId) throws DatabaseException {
		try {
			// TODO Check if project exists is valid
			this.update("DELETE FROM projects WHERE ProjectID = " + projectId
					+ ";");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// throw new DatabaseException("PROJECT_DOES_NOT_EXIST"); based on
			// the above checks
		}
	}

	public void closeAccount(String username) throws DatabaseException {
		try {
			// TODO Check if project exists is valid
			// TODO Delete all documents using the deleted project IDs
			this.update(
					"DELETE FROM users WHERE Username = '" + username + "';");
			this.update("DELETE FROM projects WHERE Username = '" + username
					+ "';");
			this.update("DELETE FROM sessions WHERE Username = '" + username
					+ "';");
			this.update(
					"DELETE FROM access WHERE Username = '" + username + "';");
			// TODO Delete all the actual file data from disk
		} catch (SQLException e) {
			e.printStackTrace();
			// throw new DatabaseException("USERNAME_DOES_NOT_EXIST"); based on
			// the above checks
		}
	}

	public void changePassword(String username, String newPassword)
			throws DatabaseException {
		try {
			this.update("UPDATE users SET Password = '" + newPassword
					+ "' WHERE Username = '" + username + "';");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// throw new DatabaseException("USERNAME_DOES_NOT_EXIST"); based on
			// the above checks
		}
	}

	public void sessionExists(String username, String serialNumber) {
		// TODO Auto-generated method stub

	}
}
