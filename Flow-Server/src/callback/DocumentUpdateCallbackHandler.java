package callback;

import message.Data;
import network.DataSocket;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Netdex on 1/15/2016.
 */
public class DocumentUpdateCallbackHandler extends CallbackHandler {
    private UUID documentUUID;

    public DocumentUpdateCallbackHandler(PersistentClientHandle handle, UUID documentUUID) {
        super(handle);
        this.documentUUID = documentUUID;
    }

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
        data.put("mod_type", event.TYPE.toString());
        data.put("line", event.LINE);
        data.put("idx", event.INDEX);
        switch (event.TYPE) {
            case INSERT:
                data.put("str", event.ADDITION);
                break;
            case DELETE:
                data.put("len", event.REMOVAL_LENGTH);
                break;
        }
        try {
            dataSocket.send(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
