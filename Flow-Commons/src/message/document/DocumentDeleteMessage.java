package message.document;

/**
 * Represents a message for the deletion of a character from a remote document
 * Created by Netdex on 12/18/2015.
 */
public class DocumentDeleteMessage extends DocumentMessage {

    private int lineNumber;
    private int idx;

    public DocumentDeleteMessage() {
        super(DocumentMessageType.CHARACTER_DELETE);
    }

    public DocumentDeleteMessage(int lineNumber, int idx) {
        super(DocumentMessageType.CHARACTER_DELETE);
        this.lineNumber = lineNumber;
        this.idx = idx;
    }

    /**
     * Gets the line number this message corresponds to
     *
     * @return the line number this message corresponds to
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Gets the index in the line this message corresponds to
     *
     * @return the index in the line this message corresponds to
     */
    public int getIndex() {
        return idx;
    }
}
