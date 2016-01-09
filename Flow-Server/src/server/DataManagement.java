package server;

import struct.FlowFile;
import struct.FlowProject;
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
    private File dataFile;

    public static DataManagement getInstance() {
        if (instance == null)
            instance = new DataManagement();
        return instance;
    }

    private DataManagement() {

    }

    public void init(File dataFile) {
        this.dataFile = dataFile;
        L.info("loading data files from file");
        if (!dataFile.exists())
            dataFile.mkdir();
    }

    public boolean addUser(User u) {
        L.info("adding user " + u);
        File d = new File(dataFile.getAbsolutePath() + File.separator + u.getUsername());
        if (d.exists())
            return false;
        d.mkdir();
        FileSerializer fs = new FileSerializer();
        fs.writeToFile(new File(d.getAbsolutePath() + File.separator + "user.flow"), u);
        return true;
    }

    public boolean removeUser(String username) {
        L.info("removing user");
        for (File f : dataFile.listFiles()) {
            if (f.isDirectory() && f.getName().equals(username)) {
                f.delete();
                return true;
            }
        }
        return false;
    }

    public User getUserByUsername(String username) {
        L.info("getting user " + username + " by username");
        File dir = new File(dataFile, username);
        if (!dir.exists())
            return null;
        FileSerializer fs = new FileSerializer();
        return fs.readFromFile(new File(dir, "user.flow"), User.class);
    }

    public boolean addProjectToUser(String username, FlowProject project) {
        File f = new File(dataFile.getAbsolutePath() + File.separator + username + File.separator
                + project.getProjectUUID().toString());
        if (f.exists())
            return false;
        f.mkdir();
        FileSerializer fs = new FileSerializer();
        File fff = new File(f, "project.flow");
        fs.writeToFile(fff, project);
        return true;
    }

    public boolean removeProjectFromUser(String username, UUID uuid) {
        File f = new File(dataFile.getAbsolutePath() + File.separator + username + File.separator
                + uuid.toString());
        if (!f.exists())
            return false;
        f.delete();
        return true;
    }

    public FlowProject getProjectFromUUID(String username, UUID uuid) {
        L.info("getting project with uuid " + uuid + " owned by " + username);
        File f = new File(dataFile.getAbsolutePath() + File.separator + username + File.separator
                + uuid.toString());
        if (!f.exists())
            return null;
        File fff = new File(f, "project.flow");
        FileSerializer fs = new FileSerializer();
        FlowProject p = fs.readFromFile(fff, FlowProject.class);
        return p;
    }

    public boolean addTextFileToProject(String username, UUID projectUUID, TextDocument textDoc) {
        File f = new File(dataFile.getAbsolutePath() + File.separator + username + File.separator
                + projectUUID.toString() + File.separator
                + textDoc.getParentFile().getParentDirectory().getFullyQualifiedPath());
        if (f.exists())
            return false;
        f.mkdirs();
        FileSerializer fs = new FileSerializer();
        File ff = new File(f.getAbsolutePath() + File.separator + textDoc.getUUID());
        if (ff.exists())
            return false;
        fs.writeToFile(ff, textDoc);
        return true;
    }

    public boolean removeTextFileFromProject(String username, UUID projectUUID,
                                             TextDocument textDocument) {
        File f = new File(dataFile.getAbsolutePath() + File.separator + username + File.separator
                + projectUUID.toString() + File.separator
                + textDocument.getParentFile().getParentDirectory().getFullyQualifiedPath()
                + File.separator + textDocument.getUUID().toString());
        if (!f.exists())
            return false;
        f.delete();
        return true;
    }

    public FlowFile getFileFromPath(String username, UUID projectUUID, String path, UUID fileUUID) {
        File f = new File(dataFile.getAbsolutePath() + File.separator + username + File.separator
                + projectUUID.toString() + File.separator + path + File.separator + fileUUID);
        if (!f.exists())
            return null;
        File ff = new File(f.getAbsolutePath() + File.separator + "file.flow");
        if (!ff.exists())
            return null;
        FileSerializer fs = new FileSerializer();
        FlowFile fff = fs.readFromFile(ff, FlowFile.class);
        return fff;
    }

    public TextDocument getTextDocumentFromPath(String username, UUID projectUUID, String path, UUID fileUUID, UUID versionUUID) {
        File f = new File(dataFile.getAbsolutePath() + File.separator + username + File.separator
                + projectUUID.toString() + File.separator + path + File.separator + fileUUID.toString() + File.separator + versionUUID.toString());
        if (!f.exists())
            return null;
        File ff = new File(f.getAbsolutePath() + File.separator + "file.flow");
        if (!ff.exists())
            return null;
        FileSerializer fs = new FileSerializer();
        TextDocument fff = fs.readFromFile(ff, TextDocument.class);
        return fff;
    }
}
