package struct;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Netdex on 12/24/2015.
 */
public class FlowProject extends FlowDirectory {

	private UUID projectUUID;
	private String name;
	private User owner;

	public FlowProject(String name, User owner, UUID uuid) {
		super();
		this.name = name;
		this.owner = owner;
		this.projectUUID = uuid;
	}

	public FlowProject(String name, User owner) {
		this(name, owner, UUID.randomUUID());
	}

	public User getOwner() {
		return owner;
	}

	public UUID getProjectUUID() {
		return projectUUID;
	}

	public String toString() {
		return name;
	}
}
