package message;

import java.io.Serializable;

/**
 * Represents a message (packet) between the remote client/server
 * <p>
 * Created by Netdex on 12/18/2015.
 */
public abstract class Message implements Serializable {

    private static transient final int MAGIC_SIGNATURE = 0xDEADBEEF;

    private MessageType type;

    public Message(MessageType type) {
        this.type = type;
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
