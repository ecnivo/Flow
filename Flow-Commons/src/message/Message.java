package message;

import network.Parcelable;

/**
 * Represents a 'packet' with a message
 * Created by Netdex on 12/18/2015.
 */
public abstract class Message implements Parcelable {

    private MessageType type;

    public Message(MessageType type) {
        this.type = type;
    }

    public MessageType getMessageType(){
        return type;
    }

    static enum MessageType {
        DOCUMENT_INSERT_CHARACTER,
        DOCUMENT_DELETE_CHARACTER;
    }
}
