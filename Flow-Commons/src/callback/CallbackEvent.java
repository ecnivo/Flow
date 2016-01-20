package callback;

import java.io.Serializable;
import java.util.UUID;

/**
 * Represents an event that a callback can handle
 * <p>
 * Created by Gordon Guan on 1/15/2016.
 */
public abstract class CallbackEvent implements Serializable {
    private final UUID assocUUID;
    private final CallbackEventType type;

    CallbackEvent(UUID assocUUID) {
        this.type = CallbackEventType.DOCUMENT_CALLBACK;
        this.assocUUID = assocUUID;
    }

    /**
     * The type of this event
     * @return the type of this event
     */
    public CallbackEventType getType() {
        return type;
    }

    /**
     * The UUID associated with this event
     * @return the UUID associated with this event
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
