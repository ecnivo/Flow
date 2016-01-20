package util;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import database.SQLDatabase;

/**
 * Consists of static methods used to performing repetitive tasks on various
 * forms of data, usually in the form of data extraction.
 * 
 * @version January 19th, 2016
 * @author Bimesh De Silva
 *
 */
public class DataManipulation {

	/**
	 * Returns the path to a directory, including the project folder.
	 * 
	 * @param directoryId
	 *            the UUID, in String form, of the directory which to generate
	 *            the path of.
	 * @return the path to the specified directory, starting with a
	 *         {@link File#separator file seperator} character.
	 * @throws DatabaseException
	 *             if the directory doesn't exist or there is an error accessing
	 *             the database.
	 */
	public static String getDirectoryPath(String directoryId)
			throws DatabaseException {
		String parentDirectoryId = directoryId;

		StringBuilder path = new StringBuilder();
		// Iteratively add the parent directories of the directory to the path
		do {
			directoryId = parentDirectoryId;

			// Add a file separator character if there already a directory in
			// the path
			if (path.length() > 0)
				path.insert(0, File.separator);
			path.insert(0, directoryId);
			try {
				parentDirectoryId = Results
						.toStringArray("ParentDirectoryID", SQLDatabase
								.getInstance().getDirectoryInfo(directoryId))[0];
			} catch (SQLException e) {
				e.printStackTrace();
				throw new DatabaseException(e.getMessage());
			}
		} while (!parentDirectoryId.equals(directoryId));

		return path.toString();
	}

	/**
	 * Returns the path to a file, including the project folder.
	 * 
	 * @param fileId
	 *            the UUID, in String form, of the file which to generate the
	 *            path of.
	 * @return the path to the specified file.
	 * @throws DatabaseException
	 *             if the file doesn't exist or there is an error accessing the
	 *             database.
	 */
	public static String getFilePath(String fileId) throws DatabaseException {
		ResultSet fileData = SQLDatabase.getInstance().getFileInfo(fileId);
		String parentDirectoryId = null, directoryId = null;
		try {
			parentDirectoryId = fileData.getString("ParentDirectoryID");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		StringBuilder path = new StringBuilder();

		// Iteratively add the parent directories of the file to the path
		do {
			directoryId = parentDirectoryId;
			// Add a file separator character if there already a file in
			// the path
			if (path.length() > 0)
				path.insert(0, File.separator);
			path.insert(0, directoryId);

			// Get the parent directory of the current directory
			try {
				parentDirectoryId = Results
						.toStringArray("ParentDirectoryID", SQLDatabase
								.getInstance().getDirectoryInfo(directoryId))[0];
			} catch (SQLException e) {
				e.printStackTrace();
				throw new DatabaseException(e.getMessage());
			}
		} while (!parentDirectoryId.equals(directoryId));

		return path.toString();
	}

	/**
	 * Converts the given Strings containing the string representations of UUIDs
	 * to an array of UUID objects.
	 * 
	 * @param uuids
	 *            String objects consisting of string representations of UUIDs.
	 * @return
	 * @throws IllegalArgumentException
	 *             If an on the Strings in the array do not conform to the
	 *             string representation as described in {@link UUID#toString()}
	 */
	public static UUID[] getUUIDsFromArray(String... uuids) {
		if (uuids == null)
			return new UUID[0];
		UUID[] array = new UUID[uuids.length];
		for (int i = 0; i < uuids.length; i++) {
			array[i] = UUID.fromString(uuids[i]);
		}
		return array;
	}
}
