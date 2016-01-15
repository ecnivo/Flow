package server;

import java.net.Socket;
import java.util.UUID;

/**
 * Created by Netdex on 1/15/2016.
 */
public class DocumentListenerHandle extends PersistentClientHandle {

    private UUID documentUUID;

    public DocumentListenerHandle(Socket socket, UUID documentUUID) {
        super(socket);
        this.documentUUID = documentUUID;
    }

    public UUID getDocumentUUID() {
        return documentUUID;
    }
}
