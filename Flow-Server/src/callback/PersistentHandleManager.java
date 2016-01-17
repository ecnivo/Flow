package callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Manages all persistent handles to the server
 *
 * Created by Netdex on 1/15/2016.
 */
public class PersistentHandleManager {
    private static PersistentHandleManager instance;
    private static Logger L = Logger.getLogger("PersistentHandleManager");

    public static PersistentHandleManager getInstance() {
        if (instance == null)
            instance = new PersistentHandleManager();
        return instance;
    }

    private HashMap<UUID, ArrayList<CallbackHandler>> events;

    private PersistentHandleManager() {
        this.events = new HashMap<>();
    }

    /**
     * Sends an event to all callback handlers registered with a certain UUID
     *
     * @param callbackUUID The UUID of the callback handlers
     * @param event        The event to send to them all
     */
    public void doCallbackEvent(UUID callbackUUID, CallbackEvent event) {
        L.info("running event " + event + " to callbackUUID " + callbackUUID);
        if (events.get(callbackUUID) == null) {
            L.warning("no handles for callbackUUID " + callbackUUID + "!");
        } else {
            for (CallbackHandler handler : events.get(callbackUUID)) {
                handler.onCallbackEvent(event);
            }
        }
    }

    public void registerCallbackHandler(UUID assocUUID, CallbackHandler handler) {
        if (events.get(assocUUID) == null)
            events.put(assocUUID, new ArrayList<>());
        events.get(assocUUID).add(handler);
        L.info("registered callback handler associated with handle " + assocUUID);
    }

    public void unregisterCallbackHandler(UUID assocUUID, CallbackHandler handler) {
        events.get(assocUUID).remove(handler);
        L.info("deregistered callback handler associated with handle " + assocUUID);
    }
}
