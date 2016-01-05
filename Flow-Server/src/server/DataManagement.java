package server;

import struct.User;
import util.FileSerializer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
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
        if(!dataFile.exists())
            dataFile.mkdir();
    }

    public boolean addUser(User u) {
        L.info("adding user " + u);
        File d = new File(dataFile.getAbsolutePath() + File.separator + u.getUsername());
        if(d.exists())
            return false;
        d.mkdir();
        FileSerializer fs = new FileSerializer();
        fs.writeToFile(new File(d.getAbsolutePath() + File.separator + "user.flow"), u);
        return true;
    }

    public boolean removeUser(String username){
        L.info("removing user");
        for(File f : dataFile.listFiles()){
            if(f.isDirectory() && f.getName().equals(username)){
                f.delete();
                return true;
            }
        }
        return false;
    }

    public User getUserByUsername(String username) {
        L.info("getting user " + username + " by username");
        for(File f : dataFile.listFiles()){
            if(f.isDirectory() && f.getName().equals(username)){
                FileSerializer fs = new FileSerializer();
                return fs.readFromFile(new File(f.getAbsolutePath() + File.separator + "user.flow"), User.class);
            }
        }
        return null;
    }
}
