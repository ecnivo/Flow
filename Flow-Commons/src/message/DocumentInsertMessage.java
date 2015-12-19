package message;

import network.MalformedParcelableException;
import network.Parcelable;

import java.nio.ByteBuffer;

/**
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

    public char getCharacter() {
        return c;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getIndex() {
        return idx;
    }
}
