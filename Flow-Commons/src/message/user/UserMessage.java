package message.user;

import message.Message;

/**
 * Created by Netdex on 12/22/2015.
 */
public abstract class UserMessage extends Message {
    public UserMessage(MessageType type) {
        super(type);
    }
}
