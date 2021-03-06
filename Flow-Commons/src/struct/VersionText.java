package struct;

import java.io.Serializable;

/**
 * Represents a editable text document Created by Gordon Guan on 12/18/2015.
 */
public class VersionText implements Serializable {

    private String text;

    public VersionText() {
        this.text = "";
    }

    protected VersionText(String text) {
        this.text = text;
    }
    /**
     * Insert a character at line number at index
     *
     * @param c   The character to add
     * @param idx The index to add the character at
     * @return whether or not line count was affected by this operation
     */
    public boolean insert(char c, int idx) {
        return insert(c + "", idx);
    }

    public boolean insert(String s, int idx) {
        if (idx < 0 || idx > text.length())
            throw new ArrayIndexOutOfBoundsException("index out of range");
        text = text.substring(0, idx) + s + text.substring(idx);
        return s.contains("\n");
    }

    /**
     * Remove a character at line number at index
     *
     * @param idx The index of the character to delete
     * @return whether or not line count was affected by this operation
     */
    public boolean delete(int idx) {
        return delete(idx, 1);
    }

    public boolean delete(int idx, int len) {
        if (idx < 0 || idx + len > text.length())
            throw new ArrayIndexOutOfBoundsException("index out of range");
        text = text.substring(0, idx) + text.substring(idx + len);
        return false;
    }

    /**
     * Get all the lines in the document as a string
     *
     * @return All the lines in the document as a string
     */
    public String getDocumentText() {
        return text;
    }

    /**
     * Sets the text of the document to a string
     *
     * @param str The string to set the text of the document to
     */
    public void setDocumentText(String str) {
        text = str;
    }

    /**
     * Gets a line in a document
     *
     * @param lineNumber The line number of the line to get
     * @return The line at that line number
     */
    public String getLine(int lineNumber) {
        return text.split("\n")[lineNumber];
    }

}
