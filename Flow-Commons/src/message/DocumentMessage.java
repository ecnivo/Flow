package message;

/**
 * Created by Netdex on 12/18/2015.
 */
public abstract class DocumentMessage extends Message {
    private DocumentMessageType type;

    public DocumentMessage(DocumentMessageType type) {
        super(MessageType.DOCUMENT_MESSAGE);
    }

    public DocumentMessageType getDocumentMessageType() {
        return type;
    }

    public enum DocumentMessageType {
        CHARACTER_INSERT,
        CHARACTER_DELETE;
    }

}
