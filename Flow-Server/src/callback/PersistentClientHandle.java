package callback;

import network.DataSocket;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Netdex on 1/15/2016.
 */
public class PersistentClientHandle {
    private Socket socket;

    private DataSocket dataSocket;

    public PersistentClientHandle(Socket socket) throws IOException {
        this.socket = socket;
        this.dataSocket = new DataSocket(socket);
    }

    public Socket getSocket() {
        return socket;
    }

    public DataSocket getDataSocket() {
        return dataSocket;
    }

}
