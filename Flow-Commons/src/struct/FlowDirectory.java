package struct;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Represents a directory in the flow file system
 * <p>
 * Created by Netdex on 12/29/2015.
 */
public class FlowDirectory implements Serializable {

    private FlowDirectory parent;
    private transient ArrayList<FlowDirectory> childDirectories;
    private transient ArrayList<FlowFile> childFiles;
    private UUID uuid;

    private String dirName;

    public FlowDirectory(String dirName) {
        this.childDirectories = new ArrayList<>();
        this.childFiles = new ArrayList<>();
        this.dirName = dirName;
        // TODO check directory name for invalid characters, throw exception if
        // not
    }

    public FlowDirectory(FlowDirectory parent, String dirName, UUID uuid) {
        this(dirName, uuid);
        this.parent = parent;
    }

    public FlowDirectory(FlowDirectory parent, String dirName) {
        this(dirName);
        this.parent = parent;
        this.uuid = UUID.randomUUID();
    }

    public FlowDirectory(String dirName, UUID uuid) {
        this(dirName);
        this.uuid = uuid;
    }

    public void setDirectoryName(String newName) {
        this.dirName = newName;
    }

    /**
     * Gets the uuid of this directory
     *
     * @return the uuid
     */
    public UUID getDirectoryUUID() {
        return uuid;
    }

    /**
     * Gets the parent of this directory, null if root directory
     *
     * @return the parent of this directory, null if root directory
     * @deprecated
     */
    private FlowDirectory getParent() {
        return parent;
    }

    /**
     * Adds a directory as a child of this one
     *
     * @param directory The directory to add as a child
     * @deprecated
     */
    private void addDirectory(FlowDirectory directory) {
        childDirectories.add(directory);
    }

    /**
     * Adds a file as a child of this directory
     *
     * @param file The file to add as a child
     * @deprecated
     */
    private void addFile(FlowFile file) throws DuplicateFileNameException {
        for (FlowFile childFile : childFiles) {
            if (childFile.getFileName().equals(file.getFileName()))
                throw new DuplicateFileNameException();
        }
        childFiles.add(file);
    }

    /**
     * Gets all child directories
     *
     * @return all child directories
     * @deprecated
     */
    private ArrayList<FlowDirectory> getDirectories() {
        return childDirectories;
    }

    /**
     * Gets all child files
     *
     * @return all child files
     * @deprecated
     */
    private ArrayList<FlowFile> getFiles() {
        return childFiles;
    }

    /**
     * @return the fully qualified path from root directory
     * @deprecated
     */
    private String getFullyQualifiedPath() {
        String path = "";
        FlowDirectory cd = this;
        while (cd.getParent() != null) {
            path += cd.getDirectoryUUID() + "/";
            cd = cd.getParent();
        }
        return path;
    }

    /**
     * @return the root directory
     * @deprecated
     */
    private FlowDirectory getRootDirectory() {
        FlowDirectory cd = this;
        while (cd.getParent() != null) {
            cd = cd.getParent();
        }
        return cd;
    }

    public String getDirectoryName() {
        return dirName;
    }

    @Override
    public String toString() {
        return dirName;
    }

    static class DuplicateFileNameException extends Exception {
    }

    static class InvalidFileNameException extends Exception {
    }
}
