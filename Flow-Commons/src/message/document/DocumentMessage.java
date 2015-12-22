package message.document;

import message.Message;

/**
 * Represents a message which affects a remote document
 * <p>
 * Created by Netdex on 12/18/2015.
 */
public abstract class DocumentMessage extends Message {

    private DocumentMessageType type;

    public DocumentMessage(DocumentMessageType type) {
        super(MessageType.DOCUMENT_MESSAGE);
        this.type = type;
    }

    /**
     * Gets the type of this document message
     *
     * @return the type of this document message
     */
    public DocumentMessageType getDocumentMessageType() {
        return type;
    }

    /**
     * Represents a type of document message
     */
    public enum DocumentMessageType {
        CHARACTER_INSERT,
        CHARACTER_DELETE;
    }

}
