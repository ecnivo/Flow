package message;

import network.MalformedParcelableException;

import java.nio.ByteBuffer;

/**
 * Created by Netdex on 12/18/2015.
 */
public abstract class DocumentMessage extends Message {

    private DocumentMessageType type;

    public DocumentMessage(DocumentMessageType type) {
        super(MessageType.DOCUMENT_MESSAGE);
        this.type = type;
    }

    public byte[] serialize(){
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
    public DocumentMessageType getDocumentMessageType() {
        return type;
    }

    public enum DocumentMessageType {
        CHARACTER_INSERT,
        CHARACTER_DELETE;
    }

}
