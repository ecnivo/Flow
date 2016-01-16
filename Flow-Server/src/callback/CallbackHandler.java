package callback;

import message.Data;
import network.DataSocket;

import java.io.IOException;

/**
 * Represents a handler that handles a callback event
 * <p>
 * Created by Netdex on 1/15/2016.
 */
public class CallbackHandler {

    public enum HandleType {
        TEXT_MODIFY
    }

    private PersistentClientHandle handle;
    private HandleType type;

    public CallbackHandler(PersistentClientHandle handle, HandleType type) {
        this.handle = handle;
        this.type = type;
    }

    public PersistentClientHandle getHandle() {
        return handle;
    }

    /**
     * An abstract callback that will be handled
     *
     * @param event The event argument passed on
     */
    public void onCallbackEvent(CallbackEvent event) {
        DataSocket dataSocket = this.getHandle().getDataSocket();
        Data data = new Data("async_callback");
        data.put("event", event);
        try {
            dataSocket.send(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HandleType getType() {
        return type;
    }
}
