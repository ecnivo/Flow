package database;

import java.sql.Driver;
import java.sql.DriverManager;

public class SQLiteTest {
	public static final String DRIVER = "org.sqlite.JDBC";

	public static void main(String[] args) {
		try {
			Driver driver = (Driver) Class.forName(DRIVER).newInstance();
			DriverManager.registerDriver(driver);
		} catch (Exception e) {
			System.out
					.println("Error loading database driver: " + e.toString());
			return;
		}
	}
}