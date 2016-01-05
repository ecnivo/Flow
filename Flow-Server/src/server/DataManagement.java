package server;

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
        for (File f : dataFile.listFiles()) {
            if (f.isDirectory() && f.getName().equals(username)) {
                FileSerializer fs = new FileSerializer();
                return fs.readFromFile(new File(f.getAbsolutePath() + File.separator + "user.flow"), User.class);
            }
        }
        return null;
    }

    public boolean addProjectToUser(String username, FlowProject project) {
        File f = new File(dataFile.getAbsolutePath() + File.separator + username + File.separator + project.getProjectUUID().toString());
        if (f.exists())
            return false;
        f.mkdir();
        FileSerializer fs = new FileSerializer();
        fs.writeToFile(f, project);
        return true;
    }

    public boolean removeProjectFromUser(String username, UUID uuid) {
        File f = new File(dataFile.getAbsolutePath() + File.separator + username + File.separator + uuid.toString());
        if (!f.exists())
            return false;
        f.delete();
        return true;
    }

    public FlowProject getProjectFromUUID(String username, UUID uuid) {
        File f = new File(dataFile.getAbsolutePath() + File.separator + username + File.separator + uuid.toString());
        if (!f.exists())
            return null;
        FileSerializer fs = new FileSerializer();
        FlowProject p = fs.readFromFile(f, FlowProject.class);
        return p;
    }

    public boolean addTextFileToProject(String username, UUID projectUUID, TextDocument textDoc) {
        File f = new File(
                dataFile.getAbsolutePath()
                        + File.separator
                        + username
                        + File.separator
                        + projectUUID.toString()
                        + File.separator
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

    public boolean removeTextFileFromProject(String username, UUID projectUUID, TextDocument textDocumentUUID) {
        File f = new File(dataFile.getAbsolutePath() + File.separator + username + File.separator + projectUUID.toString() + File.separator + textDocumentUUID.getParentFile().getParentDirectory().getFullyQualifiedPath() + File.separator + textDocumentUUID.getUUID().toString());
        if (!f.exists())
            return false;
        f.delete();
        return true;
    }
}
