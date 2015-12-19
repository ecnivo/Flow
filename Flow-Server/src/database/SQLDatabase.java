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

    /**
     * Connection to the database
     */
    private Connection connection;

    public SQLDatabase(String databaseName) throws SQLException {
	Driver driver = null;
	try {
	    driver = (Driver) Class.forName(DRIVER).newInstance();
	    DriverManager.registerDriver(driver);
	    System.out.println(driver);
	} catch (Exception e) {
	    System.out
		    .println("Error loading database driver: " + e.toString());
	    return;
	}

	this.connection = DriverManager.getConnection("jdbc:sqlite:"
		+ databaseName);
    }

    /**
     * Getter for all of the usernames in the database
     * 
     * @return all of the usernames in the database
     */
    protected ResultSet getUserNames() {
	try {
	    return this.query("select username from users");
	} catch (SQLException e) {
	    e.printStackTrace();
	    // Switch this to an empty resultset is possible
	    return null;
	}
    }

    /**
     * Authenticates a user by verifying if the specified username and password
     * pair exsists in the database
     * 
     * @param username
     *            the user's username
     * @param password
     *            the user's encrypted password
     * @return whether or not the username and password exists in the database
     */
    protected boolean authenticate(String username, String password) {
	try {
	    ResultSet pair = this
		    .query("select password from users where username = "
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
     * Add users to the database with the specified username and password
     * 
     * @param username
     *            the desired username
     * @param password
     *            the <b>encrypted</b> password <br>
     *            Please don't enter passwords in plain text
     * @return whether or not the insertion into the database was succesful. It
     *         could have been unsuccesful (returned false) because:<br>
     *         <ul>
     *         <li>The selected username already exists in the database</li>
     *         <li>An error was thrown when searching the database for all
     *         current users</li>
     *         <li>An error was thrown when inserting user into the database</li>
     *         </ul>
     */
    protected boolean addUser(String username, String password) {
	try {
	    // Checks if a user with the specified username already exsists
	    if (this.query(
		    "select username from users where username = " + username
			    + ";").next()) {
		return false;
	    }
	} catch (SQLException e) {
	    System.err.println("Error querying database from all users");
	    e.printStackTrace();
	    return false;
	}

	try {
	    this.update("insert into users (username, password) values ("
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
    public ResultSet query(String query) throws SQLException {
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
    public void update(String query) throws SQLException {
	Statement statement = this.connection.createStatement();
	statement.setQueryTimeout(TIMEOUT);
	statement.executeUpdate(query);
    }

}
