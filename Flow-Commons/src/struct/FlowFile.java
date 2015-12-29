package struct;

import java.io.Serializable;
import java.util.Date;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Created by Netdex on 12/25/2015.
 */
public class FlowFile implements Serializable {

	private FlowDirectory parentDirectory;
	private UUID uuid;

	private TreeMap<Date, FlowDocument> versions;

	public FlowFile(FlowDirectory parentDirectory) {
		this(parentDirectory, UUID.randomUUID());
	}

	public FlowFile(FlowDirectory parentDirectory, UUID uuid) {
		this.parentDirectory = parentDirectory;
		this.uuid = uuid;
		this.versions = new TreeMap<>();
	}

	public FlowDocument latest() {
		return versions.get(versions.lastKey());
	}

	public void addVersion(Date date, FlowDocument document) {
		versions.put(date, document);
	}

	public TreeMap<Date, FlowDocument> getVersions(){
		return versions;
	}

	public FlowDirectory getParentDirectory(){
		return parentDirectory;
	}

	public UUID getFileUUID() {
		return uuid;
	}
}
