package database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class SQLiteTest {

	public static void main(String[] args) throws SQLException, IOException {
		SQLDatabase database = new SQLDatabase("data/flow-database");

		BufferedReader in = new BufferedReader(
				new InputStreamReader(System.in));

		while (true) {
			switch (in.readLine().toLowerCase()) {
			case "a":
				try {
					database.update(in.readLine());
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case "b":
				try {
					ResultSet results = database.query(in.readLine());
					ResultSetMetaData meta = results.getMetaData();
					int cols = meta.getColumnCount();
					while (results.next()) {
						for (int i = 1; i <= cols; i++) {
							System.out.print(meta.getColumnName(i) + " : "
									+ results.getString(i));
							if (i < cols)
								System.out.print(", ");
						}
						System.out.println();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			default:
				System.out.println(
						"Enter 'a' for update (i.e. insert, drop, delete, create, etc. Or select 'b' for a query.");
				break;
			}
		}
	}
}