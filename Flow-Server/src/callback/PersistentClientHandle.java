package callback;

import message.Data;
import network.DataSocket;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by Gordon Guan on 1/15/2016.
 */
public class PersistentClientHandle implements Runnable {
    private static Logger L = Logger.getLogger("FLOW");

    private Socket socket;

    private DataSocket dataSocket;
    private HashMap<UUID, CallbackHandler> handlers;
    private UUID sessionUUID;

    public PersistentClientHandle(Socket socket) throws IOException {
        this.socket = socket;
        this.dataSocket = new DataSocket(socket);
        this.handlers = new HashMap<>();
    }

    public Socket getSocket() {
        return socket;
    }

    /**
     * @return the DataSocket of the persistent communication with the client
     */
    public DataSocket getDataSocket() {
        return dataSocket;
    }


    @Override
    public void run() {
        try {
            // Get the client side session ID
            sessionUUID = dataSocket.receive(UUID.class);
            while (socket.isConnected()) {
                // Get the client side request data
                Data data = dataSocket.receive(Data.class);
                L.info("accepted async data " + data);
                switch (data.get("type", String.class)) {
                    case "async":
                        // Get the UUID of the listener
                        UUID uuid = data.get("uuid", UUID.class);
                        CallbackEvent.CallbackEventType ltype = data.get("ltype", CallbackEvent.CallbackEventType.class);
                        switch (data.get("rtype", RegisterEvent.RegisterType.class)) {
                            case REGISTER: {
                                // Register the handler, and call register events
                                CallbackHandler handler = null;
                                if (ltype == CallbackEvent.CallbackEventType.DOCUMENT_CALLBACK) {
                                    handler = new DocumentUpdateCallbackHandler(this, uuid);
                                }
                                handler.onRegister(new RegisterEvent(uuid, RegisterEvent.RegisterType.REGISTER));
                                handlers.put(uuid, handler);
                                PersistentHandleManager.getInstance().registerCallbackHandler(uuid, handler);
                            }
                            break;

                            case UNREGISTER: {
                                // Unregister the handler, and call unregister events
                                CallbackHandler handler = handlers.get(uuid);
                                handler.onUnregister(new RegisterEvent(uuid, RegisterEvent.RegisterType.UNREGISTER));
                                PersistentHandleManager.getInstance().unregisterCallbackHandler(uuid, handler);
                            }
                            break;
                        }
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UUID getSessionUUID() {
        return sessionUUID;
    }
}
