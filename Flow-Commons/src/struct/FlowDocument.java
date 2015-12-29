package struct;

import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a document on the remote server
 * <p>
 * Created by Netdex on 12/24/2015.
 */
public abstract class FlowDocument implements Serializable {

    private FlowFile parentFile;
    private UUID uuid;

    protected FlowDocument(FlowFile flowFile, UUID uuid) {
        this.parentFile = flowFile;
        this.uuid = uuid;
    }

    public FlowDocument(FlowFile flowFile) {
        this(flowFile, UUID.randomUUID());
    }

    public FlowDocument() {
        this(null, UUID.randomUUID());
    }

    public void setParentFile(FlowFile file) {
        this.parentFile = file;
    }

    public FlowFile getParentFile() {
        return parentFile;
    }

    public UUID getUUID() {
        return uuid;
    }



}
