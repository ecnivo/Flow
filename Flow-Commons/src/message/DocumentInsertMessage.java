package message;

import network.MalformedParcelableException;

import java.nio.ByteBuffer;

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

    @Override
    public byte[] serialize() {
        byte[] header = super.serialize();
        ByteBuffer buffer = ByteBuffer.allocate(header.length + 2 + 4 + 4);
        buffer.put(header);
        buffer.putChar(c);
        buffer.putInt(lineNumber);
        buffer.putInt(idx);
        return buffer.array();
    }

    @Override
    public byte[] deserialize(byte[] data) throws MalformedParcelableException {
        data = super.deserialize(data);
        ByteBuffer buffer = ByteBuffer.wrap(data);
        this.c = buffer.getChar();
        this.lineNumber = buffer.getInt();
        this.idx = buffer.getInt();

        byte[] remaining = new byte[buffer.remaining()];
        buffer.get(remaining);
        return remaining;
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
