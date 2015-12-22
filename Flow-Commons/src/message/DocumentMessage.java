package message;

import network.MalformedParcelableException;

import java.nio.ByteBuffer;

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

    public byte[] serialize() {
        byte[] header = super.serialize();
        ByteBuffer buffer = ByteBuffer.allocate(header.length + 1);
        buffer.put(header);
        buffer.put((byte) type.ordinal());
        return buffer.array();
    }

    public byte[] deserialize(byte[] data) throws MalformedParcelableException {
        data = super.deserialize(data);
        ByteBuffer buffer = ByteBuffer.wrap(data);
        this.type = DocumentMessageType.values()[buffer.get()];

        byte[] remaining = new byte[buffer.remaining()];
        buffer.get(remaining);
        return remaining;
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
