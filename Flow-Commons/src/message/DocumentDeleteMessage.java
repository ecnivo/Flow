package message;

import network.CorruptedParcelableException;
import network.Parcelable;

import java.nio.ByteBuffer;

/**
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
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putInt(lineNumber);
        buffer.putInt(idx);
        return buffer.array();
    }

    @Override
    public Parcelable deserialize(byte[] data) throws CorruptedParcelableException {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        this.lineNumber = buffer.getInt();
        this.idx = buffer.getInt();
        return this;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getIndex() {
        return idx;
    }
}
