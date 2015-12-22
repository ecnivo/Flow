package message;

import network.MalformedParcelableException;

import java.nio.ByteBuffer;

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

    @Override
    public byte[] serialize() {
        byte[] header = super.serialize();
        ByteBuffer buffer = ByteBuffer.allocate(header.length + 4 + 4);
        buffer.put(header);
        buffer.putInt(lineNumber);
        buffer.putInt(idx);
        return buffer.array();
    }

    @Override
    public byte[] deserialize(byte[] data) throws MalformedParcelableException {
        data = super.deserialize(data);
        ByteBuffer buffer = ByteBuffer.wrap(data);
        this.lineNumber = buffer.getInt();
        this.idx = buffer.getInt();

        byte[] remaining = new byte[buffer.remaining()];
        buffer.get(remaining);
        return remaining;
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
