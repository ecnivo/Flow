package callback;

import message.Data;
import network.DataSocket;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by Netdex on 1/15/2016.
 */
public class PersistentClientHandle implements Runnable {
    private static Logger L = Logger.getLogger("PersistentHandleManager");

    private Socket socket;

    private DataSocket dataSocket;
    private HashMap<UUID, CallbackHandler> handlers;

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
            while (socket.isConnected()) {
                Data data = dataSocket.receive(Data.class);
                L.info("accepted async data " + data);
                switch (data.get("type", String.class)) {
                    case "async":
                        UUID uuid = data.get("uuid", UUID.class);
                        switch (data.get("rtype", String.class)) {
                            case "REGISTER":
                                CallbackHandler handler = new CallbackHandler(this);
                                handlers.put(uuid, handler);
                                PersistentHandleManager.getInstance().registerCallbackHandler(uuid, handler);
                                break;
                            case "UNREGISTER":
                                PersistentHandleManager.getInstance().unregisterCallbackHandler(uuid, handlers.get(uuid));
                                break;
                        }
                        break;
                }
            }
        } catch (Exception e) {

        }
    }
}
