package message;

import network.MalformedParcelableException;
import network.Parcelable;

import java.nio.ByteBuffer;

/**
 * Represents a message (packet) between the remote client/server
 * <p>
 * Created by Netdex on 12/18/2015.
 */
public abstract class Message implements Parcelable {

    private static final int MAGIC_SIGNATURE = 0xDEADBEEF;

    private MessageType type;

    public Message(MessageType type) {
        this.type = type;
    }

    public byte[] serialize() {
        ByteBuffer buffer = ByteBuffer.allocate(5);
        buffer.putInt(MAGIC_SIGNATURE);
        buffer.put((byte) type.ordinal());
        return buffer.array();
    }

    public byte[] deserialize(byte[] data) throws MalformedParcelableException {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        int magic = buffer.getInt();
        if (magic != MAGIC_SIGNATURE)
            throw new MalformedParcelableException("Message signature does not match!");

        byte ord = buffer.get();
        this.type = MessageType.values()[ord];

        byte[] remaining = new byte[buffer.limit() - buffer.position()];
        buffer.get(remaining);
        return remaining;
    }

    /**
     * Gets the general type of this message
     *
     * @return the general type of this message
     */
    public MessageType getMessageType() {
        return type;
    }

    /**
     * Types of messages
     */
    public static enum MessageType {
        DOCUMENT_MESSAGE;
    }
}
