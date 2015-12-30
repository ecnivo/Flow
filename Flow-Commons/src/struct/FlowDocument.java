package struct;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Represents a document on the remote server
 * <p>
 * Created by Netdex on 12/24/2015.
 */
public abstract class FlowDocument implements Serializable, Comparable<FlowDocument> {

    private FlowFile parentFile;
    private UUID uuid;
    private Date versionDate;

    protected FlowDocument(FlowFile flowFile, UUID uuid, Date versionDate) {
        this.parentFile = flowFile;
        this.uuid = uuid;
        this.versionDate = versionDate;
    }

    public FlowDocument(FlowFile flowFile, Date versionDate) {
        this(flowFile, UUID.randomUUID(), versionDate);
    }

    public FlowDocument() {
        this(null, UUID.randomUUID(), new Date());
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

    public Date getVersionDate() {
        return versionDate;
    }

    @Override
    public int compareTo(FlowDocument o) {
        return o.getVersionDate().compareTo(versionDate);
    }


}
