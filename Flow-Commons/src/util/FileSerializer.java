package util;

import java.io.*;

/**
 * Reads and writes serializables from files
 * <p>
 * Created by Gordon Guan on 12/25/2015.
 */
public class FileSerializer {

    public FileSerializer() {
    }

    /**
     * Writes a serializable to a file
     *
     * @param file         The file to write it to
     * @param serializable The serializable to serialize
     * @return
     */
    public boolean writeToFile(File file, Serializable serializable) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(serializable);
            oos.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Reads a serializable from a file
     *
     * @param file The file to read from
     * @param type The type of the serializable as a class
     * @param <T>  The type of the serializable
     * @return The serializable read from the file, or null if it fails
     */
    public <T extends Serializable> T readFromFile(File file, Class<T> type) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            T o = type.cast(ois.readObject());
            ois.close();
            return o;
        } catch (Exception e) {
            return null;
        }
    }
}
