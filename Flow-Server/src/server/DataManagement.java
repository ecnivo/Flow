package server;

import struct.User;
import util.FileSerializer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Netdex on 1/5/2016.
 */
public class DataManagement {
    private static DataManagement instance;

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
        FileSerializer serializer = new FileSerializer();
        users = serializer.readFromFile(dataFile, ArrayList.class);
        uDictionary.clear();
        for (User u : users)
            uDictionary.put(u.getUsername(), u);
    }

    public void save(File dataFile) {
        FileSerializer serializer = new FileSerializer();
        serializer.writeToFile(dataFile, users);
    }

    public void newBlank(File dataFile) {
        FileSerializer serializer = new FileSerializer();
        ArrayList<User> u = new ArrayList<>();
        serializer.writeToFile(dataFile, u);
    }

    public void addUser(User u) {
        users.add(u);
        uDictionary.put(u.getUsername(), u);
    }

    public User getUserByUsername(String username) {
        return uDictionary.get(username);
    }
}
