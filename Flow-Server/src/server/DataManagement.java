package server;

import struct.TextDocument;
import struct.User;
import util.FileSerializer;

import java.io.*;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by Netdex on 1/5/2016.
 */
public class DataManagement {

	private static final String USER_FILE_EXT = "fusr";
	private static final String TEXT_FILE_EXT = "ftd";

	private static DataManagement instance;
	private static Logger L = Logger.getLogger("DataManagement");
	private static FileSerializer fileSerializer = new FileSerializer();

	private File dataDir;
	private File userDir;
	private File fileDir;

	public static DataManagement getInstance() {
		if (instance == null)
			instance = new DataManagement();
		return instance;
	}

	private DataManagement() {

	}

	/**
	 * Initializes the data file directory
	 *
	 * @param dataFile
	 *            The location of the directory where data is stored
	 */
	public void init(File dataFile) {
		this.dataDir = dataFile;
		this.userDir = new File(dataDir, "users");
		this.fileDir = new File(dataDir, "files");

		L.info("loading data files from file");
		if (!dataFile.exists())
			dataFile.mkdirs();
		if (!userDir.exists())
			userDir.mkdirs();
		if (!fileDir.exists())
			fileDir.mkdirs();
	}

	/**
	 * Adds a user to the data files
	 *
	 * @param u
	 *            The user
	 * @return whether or not the operation succeeded
	 */
	public boolean addUser(User u) {
		L.info("adding user " + u);
		fileSerializer.writeToFile(
				new File(userDir, u.getUsername() + "." + USER_FILE_EXT), u);
		return true;
	}

	public boolean removeUser(String username) {
		L.info("removing user");
		File userFile = new File(userDir, username + "." + USER_FILE_EXT);
		return userFile.delete();
	}

	public User getUserByUsername(String username) {
		L.info("getting user " + username + " by username");
		File userFile = new File(userDir, username + "." + USER_FILE_EXT);
		if (!userFile.exists())
			return null;
		return fileSerializer.readFromFile(userFile, User.class);
	}

	public boolean addTextDocument(UUID fileUUID, TextDocument textDoc)
			throws FileNotFoundException {
		L.info("adding text document of uuid " + textDoc.getUUID() + " of file "
				+ fileUUID);
		File textFile = new File(new File(fileDir, fileUUID.toString()),
				textDoc.getUUID() + "." + TEXT_FILE_EXT);
		if (textFile.exists())
			return false;
		textFile.getParentFile().mkdirs();
		PrintStream ps = new PrintStream(textFile);
		ps.print(textDoc.getDocumentText());
		return true;
	}

	public boolean removeTextDocument(UUID fileUUID, UUID versionUUID) {
		L.info("removing text document of uuid " + versionUUID + " of file "
				+ fileUUID);
		File textFile = new File(new File(fileDir, fileUUID.toString()),
				versionUUID + "." + TEXT_FILE_EXT);
		if (!textFile.exists())
			return false;
		return textFile.delete();
	}

	public TextDocument getTextDocument(UUID fileUUID, UUID versionUUID)
			throws IOException {
		L.info("getting text document of uuid " + versionUUID + " of file "
				+ fileUUID);
		File textFile = new File(new File(fileDir, fileUUID.toString()),
				versionUUID + "." + TEXT_FILE_EXT);
		if (!textFile.exists())
			return null;
		TextDocument td = new TextDocument(versionUUID);
		BufferedReader br = new BufferedReader(new FileReader(textFile));
		String text = "";
		String line;
		while ((line = br.readLine()) != null) {
			text += line;
		}
		td.setDocumentText(text);
		return td;
	}
}
