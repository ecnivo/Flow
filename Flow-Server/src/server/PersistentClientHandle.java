package server;

import java.net.Socket;

/**
 * Created by Netdex on 1/15/2016.
 */
public abstract class PersistentClientHandle {
    private Socket socket;

    public PersistentClientHandle(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

}
