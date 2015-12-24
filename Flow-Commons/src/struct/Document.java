package struct;

import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a document on the remote server
 *
 * Created by Netdex on 12/24/2015.
 */
public abstract class Document implements Serializable {

    private UUID uuid;
    private String remotePath;

    protected Document(String remotePath, UUID uuid){
        this.remotePath = remotePath;
        this.uuid = uuid;

    }
    protected Document(String remotePath){
        this(remotePath, UUID.randomUUID());
    }

    public String getRemotePath(){
        return remotePath;
    }
}
