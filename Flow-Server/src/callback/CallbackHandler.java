package callback;

import message.Data;
import network.DataSocket;

import java.io.IOException;

/**
 * Represents a handler that handles a callback event
 *
 * Created by Netdex on 1/15/2016.
 */
public class CallbackHandler {

    private PersistentClientHandle handle;

    public CallbackHandler(PersistentClientHandle handle) {
        this.handle = handle;
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
}
