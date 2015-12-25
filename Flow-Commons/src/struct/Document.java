package struct;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Represents a document on the remote server
 *
 * Created by Netdex on 12/24/2015.
 */
public abstract class Document implements Serializable {

    private FlowFile parentFile;
    private UUID uuid;

    protected Document(FlowFile flowFile, UUID uuid){
        this.parentFile = flowFile;
        this.uuid = uuid;
    }

    public Document(){
        this(null, UUID.randomUUID());
    }

    public void setParentFile(FlowFile file){
        this.parentFile = file;
    }

    public FlowFile getParentFile(){
        return parentFile;
    }

    public UUID getUUID(){
        return uuid;
    }


}
