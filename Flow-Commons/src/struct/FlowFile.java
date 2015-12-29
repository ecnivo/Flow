package struct;

import java.io.Serializable;
import java.util.Date;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Created by Netdex on 12/25/2015.
 */
public class FlowFile implements Serializable {
	private String remotePath;
	private String remoteName;
	private UUID uuid;

	private TreeMap<Date, FlowDocument> versions;

	public FlowFile(String remotePath, String remoteName) {
		this(remotePath, remoteName, UUID.randomUUID());
	}

	public FlowFile(String remotePath, String remoteName, UUID uuid) {
		this.remoteName = remoteName;
		this.remotePath = remotePath;
		this.uuid = uuid;
		this.versions = new TreeMap<>();
	}

	public FlowDocument latest() {
		return versions.get(versions.lastKey());
	}

	public void addVersion(Date date, FlowDocument document) {
		versions.put(date, document);
	}

	public String getRemotePath() {
		return remotePath;
	}

	public String getRemoteName() {
		return remoteName;
	}

	public UUID getFileUUID() {
		return uuid;
	}
}
