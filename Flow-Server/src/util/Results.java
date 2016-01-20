package util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * Consists of static methods to parse ResultSet objects into more usable data
 * forms.
 * 
 * @author Bimesh De Silva
 *
 */
public class Results {

	/**
	 * Converts a ResultSet object into a two dimensional String array using the
	 * specified column names to retrieve the data from the ResulSet.
	 * 
	 * @param columnNames
	 *            the names of the columns to retrieve data from.
	 * @param results
	 *            the ResultSet object recieved from querying the database.
	 * @return two dimensional String array of the following dimensions: [Number
	 *         of results in ResultSet][Number of columns in columnNames
	 *         String[]]
	 * @throws SQLException
	 *             if their is an error retrieving data from the ResultSet
	 *             object.
	 */
	public static String[][] toStringArray(String[] columnNames,
			ResultSet results) throws SQLException {
		// Use a linked list because the number of results is unknown (not
		// possible to find without iterating through the ResultSet)
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
	 * Converts a ResultSet object into a String array using the specified
	 * column name to retrieve the data from the ResulSet.
	 * 
	 * @param columnName
	 *            the name of the column to retrieve data from.
	 * @param results
	 *            the ResultSet object recieved from querying the database.
	 * @return String array of the same length as the number of results in teh
	 *         ResulSet object.
	 * @throws SQLException
	 *             if their is an error retrieving data from the ResultSet
	 *             object.
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
	 * Converts a ResultSet object into a two dimensional String array using all
	 * of the available columns.
	 * 
	 * @param results
	 *            the ResultSet object recieved from querying the database.
	 * @return two dimensional String array of the following dimensions: [Number
	 *         of results in ResultSet][Number of columns in the ResultSet]
	 * @throws SQLException
	 *             if their is an error retrieving data from the ResultSet
	 *             object.
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

		// Check if the list is empty to prevent list.toArray from returning
		// null as the array
		return list.size() > 0 ? list.toArray(new String[][] { {} })
				: new String[][] { {} };
	}
}
