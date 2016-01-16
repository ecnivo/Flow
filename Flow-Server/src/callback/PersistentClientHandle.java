package callback;

import message.Data;
import network.DataSocket;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

/**
 * Created by Netdex on 1/15/2016.
 */
public class PersistentClientHandle implements Runnable {
    private Socket socket;

    private DataSocket dataSocket;

    public PersistentClientHandle(Socket socket) throws IOException {
        this.socket = socket;
        this.dataSocket = new DataSocket(socket);
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
                switch (data.get("type", String.class)) {
                    case "async":
                        UUID uuid = data.get("uuid", UUID.class);
                        switch (data.get("rtype", String.class)) {
                            case "REGISTER":
                                PersistentHandleManager.getInstance().registerCallbackHandler(uuid, new CallbackHandler(this));
                                break;
                            case "UNREGISTER":
                                break;
                        }
                        break;
                }
            }
        } catch (Exception e) {

        }
    }
}
