package struct;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Netdex on 12/24/2015.
 */
public class FlowProject implements Serializable {
    private UUID projectUUID;
    private String name;
    private User owner;

    public FlowProject(String name, User owner) {
	this.name = name;
	this.owner = owner;
	this.projectUUID = UUID.randomUUID();
    }

    public UUID getProjectUUID() {
        return projectUUID;
    }

    public FlowProject() {
    }

    public String toString() {
	return name;
    }
}
