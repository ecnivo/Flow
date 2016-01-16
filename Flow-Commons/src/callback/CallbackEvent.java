package callback;

import java.io.Serializable;
import java.util.UUID;

/**
 * Represents an event that a callback can handle
 * <p>
 * Created by Netdex on 1/15/2016.
 */
public abstract class CallbackEvent implements Serializable {
    private UUID assocUUID;

    public enum CallbackEventType {
        DOCUMENT_CALLBACK
    }

    private CallbackEventType type;

    protected CallbackEvent(CallbackEventType type, UUID assocUUID) {
        this.type = type;
        this.assocUUID = assocUUID;
    }

    public CallbackEventType getType() {
        return type;
    }

    public UUID getAssociatedUUID() {
        return assocUUID;
    }
}
