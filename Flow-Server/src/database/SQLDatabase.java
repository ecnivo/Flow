package database;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLDatabase {
	public static final String DRIVER = "org.sqlite.JDBC";

	/**
	 * Number of seconds to allow for seaching before timeout
	 */
	public static final int TIMEOUT = 5;

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

		this.connection = DriverManager
				.getConnection("jdbc:sqlite:" + databaseName);
	}

	protected ResultSet query(String query) throws SQLException {
		Statement statement = connection.createStatement();
		statement.setQueryTimeout(TIMEOUT);
		return statement.executeQuery(query);
	}

	protected void update(String query) throws SQLException {
		Statement statement = connection.createStatement();
		statement.setQueryTimeout(TIMEOUT);
		statement.executeUpdate(query);
	}

}
