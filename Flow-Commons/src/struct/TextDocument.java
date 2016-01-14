package struct;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Represents a editable text document Created by Netdex on 12/18/2015.
 */
public class TextDocument implements Serializable, Comparable<TextDocument>{

	private UUID uuid;
	private Date versionDate;

	private ArrayList<String> lines;

	public TextDocument(UUID uuid, Date versionDate) {
		this.uuid = uuid;
		this.versionDate = versionDate;
		this.lines = new ArrayList<>();
		lines.add("");
	}

	public TextDocument(Date versionDate) {
		this(UUID.randomUUID(), versionDate);
		this.lines = new ArrayList<>();
		lines.add("");
	}

    public UUID getUUID() {
        return uuid;
    }

    public Date getVersionDate() {
        return versionDate;
    }

    @Override
	public int compareTo(TextDocument o) {
		return o.getVersionDate().compareTo(versionDate);
    }


	/**
	 * Insert a character at line number at index
	 *
	 * @param c
	 *            The character to add
	 * @param lineNumber
	 *            The line number to add the character
	 * @param idx
	 *            The index to add the character at
	 * @return whether or not line count was affected by this operation
	 */
	public boolean insert(char c, int lineNumber, int idx) {
		if (lineNumber >= lines.size() || lineNumber < 0)
			throw new ArrayIndexOutOfBoundsException(
					"Line number is out of range");
		String line = lines.get(lineNumber);
		if (idx < 0 || idx > line.length())
			throw new ArrayIndexOutOfBoundsException(
					"Index in line is out of range");
		if (c == '\n') {
			String oldLine = line.substring(0, idx);
			String newLine = line.substring(idx);
			lines.set(lineNumber, oldLine);
			lines.add(lineNumber + 1, newLine);
			return true;
		} else {
			line = line.substring(0, idx) + c + line.substring(idx);
			lines.set(lineNumber, line);
			return false;
		}
	}

	/**
	 * Remove a character at line number at index
	 *
	 * @param lineNumber
	 *            The line number to delete the character
	 * @param idx
	 *            The index of the character to delete, -1 to remove the line
	 * @return whether or not line count was affected by this operation
	 */
	public boolean delete(int lineNumber, int idx) {
		if (lineNumber >= lines.size() || lineNumber < 0)
			throw new ArrayIndexOutOfBoundsException(
					"Line number is out of range");
		String line = lines.get(lineNumber);
		if (idx == -1) {
			lines.remove(lineNumber);
			return true;
		} else {
			if (idx < 0 || idx >= line.length())
				throw new ArrayIndexOutOfBoundsException(
						"Index in line is out of range");
			line = line.substring(0, idx) + line.substring(idx + 1);
			lines.set(lineNumber, line);
			return false;
		}
	}

	/**
	 * Get all the lines in the document as a string
	 *
	 * @return All the lines in the document as a string
	 */
	public String getDocumentText() {
		String str = "";
		for (String s : lines)
			str += s + '\n';
		return str;
	}

	/**
	 * Sets the text of the document to a string
	 *
	 * @param str
	 *            The string to set the text of the document to
	 */
	public void setDocumentText(String str) {
		lines.clear();
		lines.add("");
		int lineIdx = 0;
		int idx = 0;
		for (char c : str.toCharArray()) {
			if (insert(c, lineIdx, idx++)) {
				lineIdx++;
				idx = 0;
			}
		}
	}

	/**
	 * Gets a line in a document
	 *
	 * @param lineNumber
	 *            The line number of the line to get
	 * @return The line at that line number
	 */
	public String getLine(int lineNumber) {
		return lines.get(lineNumber);
	}

}
