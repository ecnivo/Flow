package callback;

import message.Data;
import network.DataSocket;

import java.io.IOException;
import java.util.UUID;

/**
 * A callback handler specifically handling document modification
 * Created by Netdex on 1/15/2016.
 */
public class DocumentUpdateCallbackHandler extends CallbackHandler {
    private UUID documentUUID;

    public DocumentUpdateCallbackHandler(PersistentClientHandle handle, UUID documentUUID) {
        super(handle, HandleType.TEXT_MODIFY);
        this.documentUUID = documentUUID;
    }

    /**
     * @return the document UUID this callback handler is associated with
     */
    public UUID getDocumentUUID() {
        return documentUUID;
    }

    @Override
    public void onCallbackEvent(CallbackEvent arg0) {
        if (!(arg0 instanceof DocumentCallbackEvent))
            throw new IllegalArgumentException("event must be document callback event!");
        DocumentCallbackEvent event = (DocumentCallbackEvent) arg0;
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
