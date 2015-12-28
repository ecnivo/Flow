package struct;

import java.io.Serializable;

/**
 * Created by Netdex on 12/24/2015.
 */
public class FlowProject implements Serializable {
	private String name;
	private User owner;

	public FlowProject(String name, User owner) {
		this.name = name;
		this.owner = owner;
	}

	public FlowProject() {
	}
}
