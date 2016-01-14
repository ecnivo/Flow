package util;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import database.SQLDatabase;

public class DataModification {

	public static String getDirectoryPath(String directoryId)
			throws DatabaseException {
		String parentDirectoryId = directoryId;

		// TODO optimize for efficiency
		StringBuilder path = new StringBuilder();
		do {
			directoryId = parentDirectoryId;
			if (path.length() > 0)
				path.insert(0, File.separator);
			path.insert(0, directoryId);
			try {
				parentDirectoryId = Results.toStringArray("ParentDirectoryID",
						SQLDatabase.getInstance()
								.getDirectoryInfo(directoryId))[0];
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new DatabaseException(e.getMessage());
			}
		} while (!parentDirectoryId.equals(directoryId));

		return path.toString();
	}

	public static String getFilePath(String fileId) throws DatabaseException {
		ResultSet fileData = SQLDatabase.getInstance().getFile(fileId);
		String parentDirectoryId = null, directoryId = null;
		try {
			parentDirectoryId = fileData.getString("ParentDirectoryID");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO optimize for efficiency
		StringBuilder path = new StringBuilder();
		do {
			directoryId = parentDirectoryId;
			if (path.length() > 0)
				path.insert(0, File.separator);
			path.insert(0, directoryId);
			try {
				parentDirectoryId = Results.toStringArray("ParentDirectoryID",
						SQLDatabase.getInstance()
								.getDirectoryInfo(directoryId))[0];
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new DatabaseException(e.getMessage());
			}
		} while (!parentDirectoryId.equals(directoryId));

		return path.toString();
	}

	public static void fileVisualizer(String directoryId)
			throws DatabaseException {
		String parentDirectoryId = directoryId;
		do {
			directoryId = parentDirectoryId;
			System.out.println(directoryId + ": ");
			// String[] data = SQLDatabase.getInstance().
			try {
				parentDirectoryId = Results.toStringArray("ParentDirectoryID",
						SQLDatabase.getInstance()
								.getDirectoryInfo(directoryId))[0];

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new DatabaseException(e.getMessage());
			}
		} while (!directoryId.equals(parentDirectoryId));
	}

	public static UUID[] getUUIDsFromArray(String... uuids) {
		UUID[] array = new UUID[uuids.length];
		for (int i = 0; i < uuids.length; i++) {
			array[i] = UUID.fromString(uuids[i]);
		}
		return array;
	}
}
