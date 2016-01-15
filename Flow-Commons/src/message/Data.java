package message;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Represents a message with an unlimited amount of properties
 * <p>
 * Created by Netdex on 12/22/2015.
 */
public class Data implements Serializable {

    private HashMap<String, Object> stringObjectHashMap;

    public Data() {
        this.stringObjectHashMap = new HashMap<>();
    }

    public Data(String type) {
        this();
        stringObjectHashMap.put("type", type);
    }

    /**
     * Gets the type of this message
     *
     * @return The type of this message
     */
    public String getType() {
        return (String) stringObjectHashMap.get("type");
    }

    /**
     * Adds a property to this message
     *
     * @param key The key to retrieve the property with
     * @param o   The value of the property, must be serializable!
     */
    public void put(String key, Serializable o) {
        stringObjectHashMap.put(key, o);
    }

    /**
     * Retrieves a property from this message
     *
     * @param key  The key of the message
     * @param type The class type of the message, for example String.class
     * @param <T>  The type of the value of the message
     * @return The value of the property of type 'type'
     */
    public <T extends Serializable> T get(String key, Class<T> type) {
        return type.cast(stringObjectHashMap.get(key));
    }

    /**
     * Retrieves a property from this message with unchecked auto cast
     *
     * @param key The key of the message
     * @param <T> The type of the value of the message
     * @return The value of the property of type 'type'
     */
    public <T extends Serializable> T get(String key) {
        return (T) stringObjectHashMap.get(key);
    }

    /**
     * Check if this data contains the key
     *
     * @param key The key to check
     * @return whether this data contains the key
     */
    public boolean containsKey(String key) {
        return stringObjectHashMap.containsKey(key);
    }

    @Override
    public String toString() {
        String str = "";
        for (String key : stringObjectHashMap.keySet()) {
            if (stringObjectHashMap.get(key) instanceof Object[]) {
                str += key + "=" + Arrays.toString((Object[]) stringObjectHashMap.get(key));
            } else {
                str += key + "=" + stringObjectHashMap.get(key).toString();
            }
            str += ", ";
        }
        return "{" + str.substring(0, str.length() - 2) + "}";
    }
}
