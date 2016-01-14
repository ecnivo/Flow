package struct;

import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a directory in the flow file system
 * <p>
 * 
 * @deprecated Created by Netdex on 12/29/2015.
 */
public class FlowDirectory implements Serializable {

	private UUID uuid;

	private String dirName;

	public FlowDirectory(String dirName) {
		this(dirName, UUID.randomUUID());
	}

	public FlowDirectory(String dirName, UUID uuid) {
		this.dirName = dirName;
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
