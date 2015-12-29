package struct;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a directory in the flow file system
 *
 * Created by Netdex on 12/29/2015.
 */
public class FlowDirectory implements Serializable {

    private FlowDirectory parent;
    private ArrayList<FlowDirectory> childDirectories;
    private ArrayList<FlowFile> childFiles;

    public FlowDirectory() {
	this.childDirectories = new ArrayList<>();
	this.childFiles = new ArrayList<>();
    }

    public FlowDirectory(FlowDirectory parent) {
	this();
	this.parent = parent;
    }

    /**
     * Gets the parent of this directory, null if root directory
     * 
     * @return the parent of this directory, null if root directory
     */
    public FlowDirectory getParent() {
	return parent;
    }

    /**
     * Adds a directory as a child of this one
     * 
     * @param directory
     *            The directory to add as a child
     */
    public void addDirectory(FlowDirectory directory) {
	childDirectories.add(directory);
    }

    /**
     * Adds a file as a child of this directory
     * 
     * @param file
     *            The file to add as a child
     */
    public void addFile(FlowFile file) {
	childFiles.add(file);
    }

    /**
     * Gets all child directories
     * 
     * @return all child directories
     */
    public ArrayList<FlowDirectory> getDirectories() {
	return childDirectories;
    }

    /**
     * Gets all child files
     * 
     * @return all child files
     */
    public ArrayList<FlowFile> getFiles() {
	return childFiles;
    }

    public FlowProject getProject() {
	FlowDirectory dir = this;
	while (!(dir instanceof FlowProject)) {
	    dir = dir.getParent();
	}
	return (FlowProject) dir;
    }
}
