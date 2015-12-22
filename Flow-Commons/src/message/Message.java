package message;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Represents a message with an unlimited amount of properties
 *
 * Created by Netdex on 12/22/2015.
 */
public class Message implements Serializable {

    private HashMap<String, Object> stringObjectHashMap;

    public Message(){
        this.stringObjectHashMap = new HashMap<>();
    }

    /**
     * Adds a property to this message
     * @param key The key to retrieve the property with
     * @param o The value of the property, must be serializable!
     */
    public void put(String key, Serializable o){
        stringObjectHashMap.put(key, o);
    }

    /**
     * Retrieves a property from this message
     * @param key The key of the message
     * @param type The class type of the message, for example String.class
     * @return The value of the property of type 'type'
     */
    public Object get(String key, Class<?> type){
        return type.cast(stringObjectHashMap.get(key));
    }
}
