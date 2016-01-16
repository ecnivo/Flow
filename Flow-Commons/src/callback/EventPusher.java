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

    public EventPusher(DataSocket arcSocket) {
        this.arcSocket = arcSocket;
        this.registeredEvents = new HashMap<>();
    }

    @Override
    public void run() {
        while (arcSocket.getSocket().isConnected()) {
            try {
                Data data = arcSocket.receive(Data.class);
                CallbackEvent event = data.get("event", CallbackEvent.class);
                UUID assocUUID = event.getAssociatedUUID();
                CallbackListener listener = registeredEvents.get(assocUUID);
                if (listener != null)
                    listener.onCallbackEvent(event);
                else
                    L.warning("warning: server tried to send event that is unregistered!");
            } catch (Exception e) {
                L.severe("error in event pusher");
                e.printStackTrace();
            }
        }
    }

    public void registerListener(UUID uuid, CallbackListener event) {
        registeredEvents.put(uuid, event);
    }

    public void unregisterListener(UUID uuid) {
        registeredEvents.put(uuid, null);
    }
}
