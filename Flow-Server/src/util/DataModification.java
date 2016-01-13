package util;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.SQLDatabase;

public class DataModification {

	public static String getDirectoryPath(String directoryId)
			throws DatabaseException {
		String parentDirectoryId = directoryId;

		// TODO optimize for efficiency
		StringBuilder path = new StringBuilder();
		do {
			directoryId = parentDirectoryId;
			try {
				parentDirectoryId = Results.toStringArray("ParentDirectoryID",
						SQLDatabase.getInstance()
								.getDirectoryInfo(directoryId))[0];
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new DatabaseException(e.getMessage());
			}
			if (path.length() > 0)
				path.append(File.separator);
			path.append(directoryId);
		} while (!parentDirectoryId.equals(directoryId));

		return path.toString();
	}

	public String getFilePath(String fileId) throws DatabaseException {
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
			try {
				parentDirectoryId = Results.toStringArray("ParentDirectoryID",
						SQLDatabase.getInstance()
								.getDirectoryInfo(directoryId))[0];
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new DatabaseException(e.getMessage());
			}
			if (path.length() > 0)
				path.append(File.separator);
			path.append(directoryId);
		} while (!parentDirectoryId.equals(directoryId));

		return path.toString();
	}

	public void fileVisualizer(String directoryId) throws DatabaseException {
		String parentDirectoryId = directoryId;
		do {
			directoryId = parentDirectoryId;
			System.out.println(directoryId + ": ");
			//	 String[] data = SQLDatabase.getInstance().
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
}
