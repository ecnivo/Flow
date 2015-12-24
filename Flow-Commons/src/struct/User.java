package struct;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Netdex on 12/24/2015.
 */
public class User implements Serializable {

    private String username;
    private transient String password;
    private transient UUID uuid;

    public User(String username, String password){
        this.username = username;
        this.password = password;
        this.uuid = UUID.randomUUID();
    }

    public User(String username){
        this(username, null);
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public UUID getUUID(){
        return uuid;
    }
}
