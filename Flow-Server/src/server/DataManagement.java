package server;

import java.io.File;
import java.util.Stack;
import java.util.UUID;
import java.util.logging.Logger;

import struct.FlowDirectory;
import struct.FlowFile;
import struct.FlowProject;
import struct.TextDocument;
import struct.User;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import util.FileSerializer;

/**
 * Created by Netdex on 1/5/2016.
 */
public class DataManagement {

	private static DataManagement instance;
	private static Logger L = Logger.getLogger("DataManagement");
	private static FileSerializer fileSerializer = new FileSerializer();
	private File dataFile;

	public static DataManagement getInstance() {
		if (instance == null)
			instance = new DataManagement();
		return instance;
	}

	private DataManagement() {

	}

	/**
	 * Initializes the data file directory
	 * @param dataFile The location of the directory where data is stored
	 */
	public void init(File dataFile) {
		this.dataFile = dataFile;
		L.info("loading data files from file");
		if (!dataFile.exists())
			dataFile.mkdir();
	}

	/**
	 * Adds a user to the data files
	 * @param u The user
	 * @return whether or not the operation succeeded
	 */
	public boolean addUser(User u) {
		L.info("adding user " + u);
		File userDirectory = new File(dataFile.getAbsolutePath(), "users");
		userDirectory.mkdir();
		fileSerializer.writeToFile(new File(userDirectory.getAbsolutePath(),
				u.getUsername() + ".flow"), u);
		return true;
	}

	public boolean removeUser(String username) {
		L.info("removing user");
		File userFile = new File(new File(dataFile, "users"),
				username + ".flow");
		return userFile.delete();
	}

	public User getUserByUsername(String username) {
		L.info("getting user " + username + " by username");
		File userFile = new File(new File(dataFile, "users"),
				username + ".flow");
		if (!userFile.exists())
			return null;
		return fileSerializer.readFromFile(userFile, User.class);
	}

	public boolean addProjectToUser(FlowProject project) {
		L.info("adding project " + project.getDirectoryName());
		File projectDirectory = new File(new File(dataFile, "projects"),
				project.getProjectUUID().toString());
		if (projectDirectory.exists())
			return false;
		projectDirectory.mkdir();
		File projectMetadataFile = new File(projectDirectory, "project.flow");
		fileSerializer.writeToFile(projectMetadataFile, project);
		return true;
	}

	public boolean removeProject(UUID uuid) {
		L.info("removing project with uuid " + uuid);
		File projectDirectory = new File(new File(dataFile, "projects"),
				uuid.toString());
		if (!projectDirectory.exists())
			return false;
		projectDirectory.delete();
		return true;
	}

	public FlowProject getProjectFromUUID(UUID uuid) {
		L.info("getting project with uuid " + uuid);
		File projectDirectory = new File(new File(dataFile, "projects"),
				uuid.toString());
		if (!projectDirectory.exists())
			return null;
		File projectMetadataFile = new File(projectDirectory, "project.flow");
		FlowProject project = fileSerializer.readFromFile(projectMetadataFile,
				FlowProject.class);
		return project;
	}

	public boolean renameProject(UUID uuid, String newName) {
		L.info("renaming project with uuid " + uuid);
		FlowProject oldProject = this.getProjectFromUUID(uuid);
		if (oldProject == null)
			return false;
		oldProject.setDirectoryName(newName);
		File projectMetadata = new File(
				new File(new File(dataFile, "projects"), uuid.toString()),
				"project.flow");
		if (!projectMetadata.exists())
			return false;
		projectMetadata.delete();
		fileSerializer.writeToFile(projectMetadata, oldProject);
		return true;
	}

	public boolean addTextDocumentToProject(UUID projectUUID, TextDocument textDoc) {
		throw new UnsupportedOperationException();
	}

	public boolean removeTextFileFromProject(String username, UUID projectUUID,
			TextDocument textDocument) {
		throw new UnsupportedOperationException();
	}

	public FlowFile getFileFromPath(UUID projectUUID, String path, UUID fileUUID) {
		throw new UnsupportedOperationException();
	}

	public TextDocument getTextDocumentFromPath(String username,
			UUID projectUUID, String path, UUID fileUUID, UUID versionUUID) {
		throw new UnsupportedOperationException();
	}
}
