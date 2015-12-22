package message;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Netdex on 12/22/2015.
 */
public class Message implements Serializable {

    private HashMap<String, Object> stringObjectHashMap;

    public Message(){
        this.stringObjectHashMap = new HashMap<>();
    }

    public void put(String key, Object o){
        stringObjectHashMap.put(key, o);
    }

    public Object get(String key, Class<?> type){
        return type.cast(stringObjectHashMap.get(key));
    }
}
