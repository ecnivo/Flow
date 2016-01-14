package server;

import struct.TextDocument;
import struct.User;
import util.FileSerializer;

import java.io.File;
import java.util.UUID;
import java.util.logging.Logger;

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
     *
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
     *
     * @param u The user
     * @return whether or not the operation succeeded
     */
    public boolean addUser(User u) {
        L.info("adding user " + u);
        File userDirectory = new File(dataFile.getAbsolutePath(), "users");
        userDirectory.mkdir();
        fileSerializer.writeToFile(new File(userDirectory.getAbsolutePath(), u.getUsername() + ".flow"), u);
        return true;
    }

    public boolean removeUser(String username) {
        L.info("removing user");
        File userFile = new File(new File(dataFile, "users"), username + ".flow");
        return userFile.delete();
    }

    public User getUserByUsername(String username) {
        L.info("getting user " + username + " by username");
        File userFile = new File(new File(dataFile, "users"), username + ".flow");
        if (!userFile.exists())
            return null;
        return fileSerializer.readFromFile(userFile, User.class);
    }

    public boolean addTextDocumentToProject(UUID projectUUID, TextDocument textDoc) {
        throw new UnsupportedOperationException();
    }

    public boolean removeTextFileFromProject(String username, UUID projectUUID, TextDocument textDocument) {
        throw new UnsupportedOperationException();
    }

    public TextDocument getTextDocumentFromPath(String username, UUID projectUUID, String path, UUID fileUUID, UUID versionUUID) {
        throw new UnsupportedOperationException();
    }
}
