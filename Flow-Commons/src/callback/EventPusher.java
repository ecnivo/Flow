package callback;

import message.Data;
import network.DataSocket;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Pushes events to client side event listeners
 * Created by Gordon Guan on 1/16/2016.
 */
public class EventPusher implements Runnable {

    private static Logger L = Logger.getLogger("EventPusher");
    private DataSocket arcSocket;

    private HashMap<UUID, CallbackListener> registeredEvents;

    private boolean running = true;

    private UUID sessionID;

    public EventPusher(DataSocket arcSocket, UUID sessionID) {
        this.arcSocket = arcSocket;
        this.sessionID = sessionID;
        this.registeredEvents = new HashMap<>();
    }

    @Override
    public void run() {
        L.info("event pusher started under session " + sessionID);
        try {
            // Tell the server the sessionID
            arcSocket.send(sessionID);
            while (arcSocket.getSocket().isConnected() && running) {
                // Receive the event
                Data data = arcSocket.receive(Data.class);
                if (running) {
                    CallbackEvent event = data.get("event", CallbackEvent.class);
                    L.info("received event " + event);
                    UUID assocUUID = event.getAssociatedUUID();
                    // Notify the registered client side listener
                    CallbackListener listener = registeredEvents.get(assocUUID);
                    if (listener != null)
                        listener.onCallbackEvent(event);
                    else
                        L.warning("server tried to send event that is unregistered! this means something is wrong!");
                }
            }
        } catch (Exception e) {
            L.severe("error in event pusher");
            e.printStackTrace();
        }
        L.info("event pusher dead");
    }

    /**
     * Kill the event pusher
     */
    public void kill() {
        running = false;
    }

    /**
     * Register a listener
     *
     * @param uuid     The UUID of the listener
     * @param listener The listener
     */
    public void registerListener(UUID uuid, CallbackListener listener) {
        registeredEvents.put(uuid, listener);
    }

    /**
     * Unregister a listener
     * @param uuid The UUID of the listener
     */
    public void unregisterListener(UUID uuid) {
        registeredEvents.put(uuid, null);
    }
}
