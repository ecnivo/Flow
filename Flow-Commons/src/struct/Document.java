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
    private String remoteName;

    protected Document(String remotePath, String remoteName, UUID uuid){
        this.remotePath = remotePath;
        this.remoteName = remoteName;
        this.uuid = uuid;


    }
    protected Document(String remotePath, String remoteName){
        this(remotePath, remoteName, UUID.randomUUID());
    }

    public String getRemotePath(){
        return remotePath;
    }

    public String getRemoteName(){
        return remoteName;
    }
}
