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

    public static DataManagement getInstance() {
        if (instance == null)
            instance = new DataManagement();
        return instance;
    }

    private ArrayList<User> users;
    private HashMap<String, User> uDictionary;

    private DataManagement() {
        uDictionary = new HashMap<>();
    }

    public void load(File dataFile) {
        L.info("loading data files from file");
        FileSerializer serializer = new FileSerializer();
        users = serializer.readFromFile(dataFile, ArrayList.class);
        uDictionary.clear();
        for (User u : users)
            uDictionary.put(u.getUsername(), u);
    }

    public void save(File dataFile) {
        L.info("saving data to file");
        FileSerializer serializer = new FileSerializer();
        serializer.writeToFile(dataFile, users);
    }

    public void newBlank(File dataFile) {
        L.info("creating blank file");
        FileSerializer serializer = new FileSerializer();
        ArrayList<User> u = new ArrayList<>();
        serializer.writeToFile(dataFile, u);
    }

    public void addUser(User u) {
        L.info("adding user " + u);
        users.add(u);
        uDictionary.put(u.getUsername(), u);
    }
    public void removeUser(User u){
        L.info("removing user");
        users.remove(u);
        uDictionary.remove(u.getUsername());
    }

    public User getUserByUsername(String username) {
        L.info("getting user " + username + " by username");
        return uDictionary.get(username);
    }
}
