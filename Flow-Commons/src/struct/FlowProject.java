package struct;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Represents a FlowProject, analagous to an eclipse project.
 * <p>
 * Created by Netdex on 12/24/2015.
 */
public class FlowProject extends FlowDirectory {

    private UUID projectUUID;

    private User owner;
    private transient ArrayList<User> viewers;
    private transient ArrayList<User> editors;

    public FlowProject(String name, User owner, UUID uuid) {
        super(name);
        this.owner = owner;
        this.projectUUID = uuid;
    }

    public FlowProject(String name, User owner) {
        this(name, owner, UUID.randomUUID());
    }

    /**
     * @return the owner of this project
     */
    public User getOwner() {
        return owner;
    }

    /**
     * @return the unique identifier of this project
     */
    public UUID getProjectUUID() {
        return projectUUID;
    }

    public String toString() {
        return this.getDirectoryName();
    }

    public ArrayList<User> getViewers() {
        return viewers;
    }

    public void addViewer(User viewer) {
        viewers.add(viewer);
    }

    public void removeViewer(User viewer) {
        viewers.remove(viewer);
    }

    public ArrayList<User> getEditors() {
        return editors;
    }

    public void addEditor(User editor) {
        editors.add(editor);
    }

    public void removeEditor(User editor) {
        editors.remove(editor);
    }
}
