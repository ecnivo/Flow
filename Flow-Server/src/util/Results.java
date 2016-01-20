package util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * Class consisting of static methods to parse ResultSet objects into more
 * usable data forms.
 * 
 * @author Bimesh De Silva
 *
 */
public class Results {

	/**
	 * 
	 * @param columnNames
	 * @param results
	 * @return
	 * @throws SQLException
	 */
	public static String[][] toStringArray(String[] columnNames,
			ResultSet results) throws SQLException {
		LinkedList<String[]> list = new LinkedList<>();
		while (results.next()) {
			String[] row = new String[columnNames.length];
			for (int i = 0; i < row.length; i++) {
				row[i] = results.getString(columnNames[i]);
			}
			list.add(row);
		}
		return list.toArray(new String[][] { {} });
	}

	/**
	 * 
	 * @param columnName
	 * @param results
	 * @return
	 * @throws SQLException
	 */
	public static String[] toStringArray(String columnName, ResultSet results)
			throws SQLException {
		String[][] arr = toStringArray(new String[] { columnName }, results);
		if (arr.length == 1 && arr[0] == null)
			return new String[0];
		String[] retarr = new String[arr.length];
		for (int i = 0; i < arr.length; i++) {
			retarr[i] = arr[i][0];
		}
		return retarr;
	}
	
	/**
	 * 
	 * @param results
	 * @return
	 * @throws SQLException
	 */
	public static String[][] toStringArray(ResultSet results)
			throws SQLException {
		int noOfColumns = results.getMetaData().getColumnCount();
		LinkedList<String[]> list = new LinkedList<>();
		while (results.next()) {
			String[] row = new String[noOfColumns];
			for (int i = 0; i < row.length; i++) {
				row[i] = results.getString(i);
			}
			list.add(row);
		}
		return list.size() > 0 ? list.toArray(new String[][] { {} })
				: new String[][] { {} };
	}
}
