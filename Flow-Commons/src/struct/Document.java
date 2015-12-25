package struct;

import java.io.Serializable;
import java.util.ArrayList;
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

    private ArrayList<Document> versions;

    protected Document(String remotePath, String remoteName, UUID uuid){
        this.remotePath = remotePath;
        this.remoteName = remoteName;
        this.uuid = uuid;
        this.versions = new ArrayList<>();
    }

    protected Document(String remotePath, String remoteName){
        this(remotePath, remoteName, UUID.randomUUID());
    }

    public ArrayList<Document> getVersions(){
        return versions;
    }

    public UUID getUUID(){
        return uuid;
    }

    public String getRemotePath(){
        return remotePath;
    }

    public String getRemoteName(){
        return remoteName;
    }
}
