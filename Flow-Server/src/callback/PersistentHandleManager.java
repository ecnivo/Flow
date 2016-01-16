package callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Manages all persistent handles to the server
 *
 * Created by Netdex on 1/15/2016.
 */
public class PersistentHandleManager {
    private static PersistentHandleManager instance;

    public static PersistentHandleManager getInstance() {
        if (instance == null)
            instance = new PersistentHandleManager();
        return instance;
    }

    private HashMap<UUID, ArrayList<CallbackHandler>> events;

    private PersistentHandleManager() {

    }

    /**
     * Sends an event to all callback handlers registered with a certain UUID
     *
     * @param callbackUUID The UUID of the callback handlers
     * @param event        The event to send to them all
     */
    public void doCallbackEvent(UUID callbackUUID, CallbackEvent event) {
        for (CallbackHandler handler : events.get(callbackUUID)) {
            handler.onCallbackEvent(event);
        }
    }

    public void registerCallbackHandler(UUID assocUUID, CallbackHandler handler) {
        if (events.get(assocUUID) == null)
            events.put(assocUUID, new ArrayList<>());
        events.get(assocUUID).add(handler);
    }
}
