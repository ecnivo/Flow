package message.document;

/**
 * Represents a message for the insertion of a chracter in a remote document
 * <p>
 * Created by Netdex on 12/18/2015.
 */
public class DocumentInsertMessage extends DocumentMessage {

    private char c;
    private int lineNumber;
    private int idx;

    public DocumentInsertMessage() {
        super(DocumentMessageType.CHARACTER_INSERT);
    }

    public DocumentInsertMessage(char c, int lineNumber, int idx) {
        super(DocumentMessageType.CHARACTER_INSERT);
        this.c = c;
        this.lineNumber = lineNumber;
        this.idx = idx;
    }

    /**
     * Gets the character to add
     *
     * @return the character to add
     */
    public char getCharacter() {
        return c;
    }

    /**
     * Gets the line number to add the character
     *
     * @return the line number to add the character
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Gets the index to add the character
     *
     * @return the index to add the character
     */
    public int getIndex() {
        return idx;
    }
}
