package database;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLDatabase {

	/**
	 * Location where the SQLite JDBC drivers are stored
	 */
	public static final String DRIVER = "org.sqlite.JDBC";

	/**
	 * Number of seconds to allow for seaching before timeout
	 */
	public static final int TIMEOUT = 5;

	public static final int ADD_USER = 0, REMOVE_USER = 1, OWNER = 0, EDIT = 1,
			VIEW = 2;

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
	 * @param userId
	 *            the ID of the user.
	 * @return all projects associated with the specified UserID.
	 */
	public ResultSet getProjects(String userId) {
		try {
			// TODO Allow for multiple users to be in permission (see
			// updatePermission())
			return this.query("SELECT ProjectId FROM access WHERE UserID = "
					+ userId + ";");
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
	 * @param userId
	 *            the ID of the user which to provide access to.
	 * @return whether or not the access was successfully granted.
	 */
	public boolean updateAccess(int accessLevel, String projectId,
			String userId) {
		// TODO Password project (ask username / password as parameter)
		try {
			if (accessLevel == EDIT || accessLevel == VIEW) {
				this.update("INSERT INTO access values(" + projectId + ", "
						+ userId + ", " + accessLevel + ";");
				return true;
			}
			if (accessLevel == OWNER) {
				// Changes the owner of the project in the projects table
				this.update("UPDATE projects SET OwnerID = " + userId
						+ " WHERE ProjectID = " + projectId + ";");

				// Changes the permissions of the user to be an owner
				this.update("INSERT INTO access values(" + projectId + ", "
						+ userId + ", " + OWNER + ");");

				// TODO remove all other permissions (i.e. if they had edit
				// permissions)
				return true;
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
	public ResultSet getFiles(String projectId) {
		try {
			return this
					.query("SELECT DocumentName FROM documents WHERE ProjectID = "
							+ projectId + ";");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
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
	 * Getter for all of the usernames in the database.
	 * 
	 * @return all of the usernames in the database.
	 */
	public ResultSet getUserNames() {
		try {
			return this.query("SELECT username FROM users;");
		} catch (SQLException e) {
			e.printStackTrace();
			// Switch this to an empty resultset is possible
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

	public boolean newSession(String userUUID, String serialNumber,
			String sessionUUID) {
		try {
			this.update("INSERT INTO sessions VALUES (" + userUUID + ", "
					+ serialNumber + ", " + sessionUUID + ");");
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean removeSession(String sessionId) {
		try {
			this.update("DELETE FROM sessions WHERE SessionID = " + sessionId
					+ ";");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public ResultSet getUserId(String username) {
		try {
			return this.query("SELECT UserID FROM users WHERE Username = "
					+ username + ";");
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
}
