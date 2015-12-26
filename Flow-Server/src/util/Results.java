package util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class Results {

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
		return (String[][]) list.toArray();
	}

	public static String[] toStringArray(String columnName, ResultSet results)
			throws SQLException {
		return toStringArray(new String[] { columnName }, results)[0];
	}

	public static int[][] toIntArray(String[] columnNames, ResultSet results)
			throws SQLException {
		LinkedList<int[]> list = new LinkedList<>();
		while (results.next()) {
			int[] row = new int[columnNames.length];
			for (int i = 0; i < row.length; i++) {
				row[i] = results.getInt(columnNames[i]);
			}
			list.add(row);
		}
		return (int[][]) list.toArray();
	}

	public static int[] toIntArray(String columnName, ResultSet results)
			throws SQLException {
		return toIntArray(new String[] { columnName }, results)[0];
	}

	public static String[][] toStringArray(int[] columnNumbers,
			ResultSet results) throws SQLException {
		LinkedList<String[]> list = new LinkedList<>();
		while (results.next()) {
			String[] row = new String[columnNumbers.length];
			for (int i = 0; i < row.length; i++) {
				row[i] = results.getString(columnNumbers[i]);
			}
			list.add(row);
		}
		return (String[][]) list.toArray();
	}

	public static String[] toStringArray(int columnNumber, ResultSet results)
			throws SQLException {
		return toStringArray(new int[] { columnNumber }, results)[0];
	}

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
		return (String[][]) list.toArray();
	}
}
