package struct;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Date;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Represents a file in a flow file system, with version control
 * <p>
 * Created by Netdex on 12/25/2015.
 */
public class FlowFile implements Serializable {

	private FlowDirectory parentDirectory;
	private UUID uuid;
	private String fileName;

	private transient TreeMap<Date, FlowDocument> versions;

	public FlowFile(FlowDirectory parentDirectory, String fileName) {
		this(parentDirectory, fileName, UUID.randomUUID());
	}

	public FlowFile(FlowDirectory parentDirectory, String fileName, UUID uuid) {
		this.parentDirectory = parentDirectory;
		this.uuid = uuid;
		this.fileName = fileName;
		this.versions = new TreeMap<>();
	}

	/**
	 * Gets the latest revision of the document stored under this file name
	 *
	 * @return the latest revision of the document stored under this file name
	 */
	public FlowDocument latest() {
		return versions.get(versions.lastKey());
	}

	/**
	 * Adds a version to the version control system
	 *
	 * @param date
	 *            The timestamp of the modification date of this document
	 * @param document
	 *            The document representing the state of the file
	 */
	public void addVersion(Date date, FlowDocument document) {
		versions.put(date, document);
	}

	/**
	 * Gets all versions
	 *
	 * @return all versions
	 */
	public TreeMap<Date, FlowDocument> getVersions() {
		return versions;
	}

	/**
	 * Gets the parent directory of this file
	 *
	 * @return the parent directory of this file
	 */
	public FlowDirectory getParentDirectory() {
		return parentDirectory;
	}

	/**
	 * Get the date of the specified document. <br>
	 * This runs in linear time.
	 * 
	 * @param document
	 *            the FlowDocument
	 * @return the Date associated with the specified FlowDocument
	 * @throws FileNotFoundException
	 *             if the specified FlowDocument isn't found
	 */
	public Date getVersion(FlowDocument document) throws FileNotFoundException {
		for (Entry<Date, FlowDocument> entry : versions.entrySet()) {
			if (document.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		throw new FileNotFoundException();
	}

	/**
	 * Gets a unique identifier for this file
	 *
	 * @return a unique identifier for this file
	 */
	public UUID getFileUUID() {
		return uuid;
	}

	public String getFileName() {
		return fileName;
	}

	@Override
	public String toString() {
		return fileName;
	}
}
