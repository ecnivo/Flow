package message;

import network.CorruptedParcelableException;
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
        ByteBuffer buffer = ByteBuffer.allocate(9);
        buffer.putChar(c);
        buffer.putInt(lineNumber);
        buffer.putInt(idx);
        return buffer.array();
    }

    @Override
    public Parcelable deserialize(byte[] data) throws CorruptedParcelableException {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        this.c = buffer.getChar();
        this.lineNumber = buffer.getInt();
        this.idx = buffer.getInt();
        return this;
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
