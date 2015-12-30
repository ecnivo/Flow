package struct;

import java.io.Serializable;
import java.util.TreeSet;
import java.util.UUID;

/**
 * Represents a file in a flow file system, with version control
 * <p>
 * Created by Netdex on 12/25/2015.
 */
public class FlowFile implements Serializable {

    private FlowDirectory parentDirectory;
    private UUID uuid;
    private String fileName;

    private transient TreeSet<FlowDocument> versions;

    public FlowFile(FlowDirectory parentDirectory, String fileName) {
        this(parentDirectory, fileName, UUID.randomUUID());
    }

    public FlowFile(FlowDirectory parentDirectory, String fileName, UUID uuid) {
        this.parentDirectory = parentDirectory;
        this.uuid = uuid;
        this.fileName = fileName;
        this.versions = new TreeSet<>();
    }

    /**
     * Gets the latest revision of the document stored under this file name
     *
     * @return the latest revision of the document stored under this file name
     */
    public FlowDocument latest() {
        return versions.last();
    }

    /**
     * Adds a version to the version control system
     *
     * @param document The document representing the state of the file
     */
    public void addVersion(FlowDocument document) {
        versions.add(document);
    }

    /**
     * Gets all versions
     *
     * @return all versions
     */
    public TreeSet<FlowDocument> getVersions() {
        return versions;
    }

    /**
     * Gets the parent directory of this file
     *
     * @return the parent directory of this file
     */
    public FlowDirectory getParentDirectory() {
        return parentDirectory;
    }

    /**
     * Gets a unique identifier for this file
     *
     * @return a unique identifier for this file
     */
    public UUID getFileUUID() {
        return uuid;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        return fileName;
    }
}
