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
     * @param dataFile The location of the directory where data is stored
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
     * @param u The user
     * @return whether or not the operation succeeded
     */
    public boolean addUser(User u) {
        L.info("adding user " + u);
        fileSerializer.writeToFile(
                new File(userDir, u.getUsername() + "." + USER_FILE_EXT), u);
        return true;
    }

    /**
     * Removes a user
     *
     * @param username The username of the user
     * @return whether or not the removal was successful
     */
    public boolean removeUser(String username) {
        L.info("removing user");
        File userFile = new File(userDir, username + "." + USER_FILE_EXT);
        return userFile.delete();
    }

    /**
     * Gets a user by their username
     *
     * @param username The user's username
     * @return The user
     */
    public User getUserByUsername(String username) {
        L.info("getting user " + username + " by username");
        File userFile = new File(userDir, username + "." + USER_FILE_EXT);
        if (!userFile.exists())
            return null;
        return fileSerializer.readFromFile(userFile, User.class);
    }

    /**
     * Adds a text document version into its file
     *
     * @param fileUUID    The UUID of the file
     * @param versionUUID The UUID of the file's version
     * @param textDoc     The text document
     * @return whether or not the addition was successful
     */
    public boolean addTextDocumentVersion(UUID fileUUID, UUID versionUUID, TextDocument textDoc) {
        L.info("adding text document of uuid " + versionUUID + " of file " + fileUUID);
        File textFile = new File(new File(fileDir, fileUUID.toString()), versionUUID + "." + TEXT_FILE_EXT);
        textFile.getParentFile().mkdirs();
        try {
            PrintStream ps = new PrintStream(textFile);
            ps.print(textDoc.getDocumentText());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Removes a text document version from its file
     *
     * @param fileUUID    The UUID of the file
     * @param versionUUID The UUID of the version of the file
     * @return whether or not the removal was successful
     */
    public boolean removeDocumentVersion(UUID fileUUID, UUID versionUUID) {
        L.info("removing document of uuid " + versionUUID + " of file "
                + fileUUID);
        File textFile = new File(new File(fileDir, fileUUID.toString()),
                versionUUID + "." + TEXT_FILE_EXT);
        return textFile.delete();
    }

    /**
     * Adds an arbitrary document into its file
     *
     * @param fileUUID    The UUID of the file
     * @param versionUUID The UUID of the version
     * @param bytes       The bytes of the file
     * @return whether or not the addition was successful
     */
    public boolean addArbitraryDocumentVersion(UUID fileUUID, UUID versionUUID, byte[] bytes) {
        L.info("adding arbitrary document of uuid " + fileUUID + " of length " + bytes.length);
        File file = new File(new File(fileDir, fileUUID.toString()), versionUUID.toString() + "." + TEXT_FILE_EXT);
        file.getParentFile().mkdirs();
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.flush();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Removes a file by its UUID
     *
     * @param fileUUID The file's UUID
     * @return whether or not the removal was successful
     */
    public boolean removeFileByUUID(UUID fileUUID) {
        L.info("removing file of uuid " + fileUUID);
        File file = new File(fileDir, fileUUID.toString());
        return file.delete();
    }

    /**
     * Gets a text document
     *
     * @param fileUUID    The UUID of the file
     * @param versionUUID The UUID of the version
     * @return The text document
     */
    public TextDocument getTextDocument(UUID fileUUID, UUID versionUUID) {
        L.info("getting text document of uuid " + versionUUID + " of file "
                + fileUUID);
        File textFile = new File(new File(fileDir, fileUUID.toString()),
                versionUUID + "." + TEXT_FILE_EXT);
        if (!textFile.exists())
            return null;
        TextDocument td = new TextDocument();
        try {
            BufferedReader br = new BufferedReader(new FileReader(textFile));
            String text = "";
            String line;
            while ((line = br.readLine()) != null) {
                text += line;
            }
            td.setDocumentText(text);
        } catch (Exception e) {
            return null;
        }
        return td;
    }

    public byte[] getArbitraryFileBytes(UUID fileUUID, UUID versionUUID) {
        L.info("getting arbitrary document of uuid " + versionUUID + " of file" + fileUUID);
        File textFile = new File(new File(fileDir, fileUUID.toString()),
                versionUUID + "." + TEXT_FILE_EXT);
        if (!textFile.exists())
            return null;
        try {
            FileInputStream fis = new FileInputStream(textFile);
            byte[] buffer = new byte[(int) textFile.length()];
            fis.read(buffer);
            return buffer;
        } catch (Exception e) {
            return null;
        }
    }
}
