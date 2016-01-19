package callback;

import java.io.Serializable;
import java.util.UUID;

/**
 * Represents an event that a callback can handle
 * <p>
 * Created by Gordon Guan on 1/15/2016.
 */
public abstract class CallbackEvent implements Serializable {
    private UUID assocUUID;
    private CallbackEventType type;

    protected CallbackEvent(CallbackEventType type, UUID assocUUID) {
        this.type = type;
        this.assocUUID = assocUUID;
    }

    /**
     * The type of this event
     * @return
     */
    public CallbackEventType getType() {
        return type;
    }

    /**
     * The UUID associated with this event
     * @return
     */
    public UUID getAssociatedUUID() {
        return assocUUID;
    }

    /**
     * The type of event
     */
    public enum CallbackEventType {
        DOCUMENT_CALLBACK
    }
}
