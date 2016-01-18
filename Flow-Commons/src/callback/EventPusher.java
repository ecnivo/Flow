package callback;

import message.Data;
import network.DataSocket;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by Netdex on 1/16/2016.
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
            arcSocket.send(sessionID);
            while (arcSocket.getSocket().isConnected() && running) {
                Data data = arcSocket.receive(Data.class);
                if (running) {
                    CallbackEvent event = data.get("event", CallbackEvent.class);
                    L.info("received event " + event);
                    UUID assocUUID = event.getAssociatedUUID();
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

    public void kill() {
        running = false;
    }
    public void registerListener(UUID uuid, CallbackListener event) {
        registeredEvents.put(uuid, event);
    }

    public void unregisterListener(UUID uuid) {
        registeredEvents.put(uuid, null);
    }
}
