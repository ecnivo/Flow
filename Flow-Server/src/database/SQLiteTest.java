package database;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteTest {
	public static final String DRIVER = "org.sqlite.JDBC";

	/**
	 * Number of seconds to allow for seaching before timeout
	 */
	public static final int TIMEOUT = 5;

	public static void main(String[] args) throws SQLException {
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

		Connection connection = DriverManager
				.getConnection("jdbc:sqlite:data/flow-users-database");

		Statement statement = connection.createStatement();
		statement.setQueryTimeout(TIMEOUT);

		statement.executeUpdate("create table");
	}
}