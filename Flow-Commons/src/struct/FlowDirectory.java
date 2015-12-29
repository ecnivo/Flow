package struct;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Netdex on 12/29/2015.
 */
public class FlowDirectory implements Serializable{

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

    public FlowDirectory getParent() {
        return parent;
    }

    public void addDirectory(FlowDirectory directory){
        childDirectories.add(directory);
    }

    public void addFile(FlowFile file){
        childFiles.add(file);
    }

    public ArrayList<FlowDirectory> getDirectories() {
        return childDirectories;
    }

    public ArrayList<FlowFile> getFiles() {
        return childFiles;
    }
}
