package server;

import callback.PersistentClientHandle;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Another server listening and dispatching asynchronous events to clients
 * Created by Gordon Guan on 1/16/2016.
 */
class AsyncServer implements Runnable {
    private final int arcport;

    public AsyncServer() {
        this.arcport = FlowServer.ARC_PORT;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(arcport);
            while (serverSocket.isBound()) {
                Socket socket = serverSocket.accept();
                // Create a new persistent client handle and start their listener
                PersistentClientHandle pch = new PersistentClientHandle(socket);
                new Thread(pch).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
