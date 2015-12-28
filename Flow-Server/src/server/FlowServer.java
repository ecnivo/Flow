package server;

import database.SQLDatabase;

import java.net.ServerSocket;
import java.net.Socket;

public class FlowServer implements Runnable {

    SQLDatabase database;

    public FlowServer() {

    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket();
            while (serverSocket.isBound()) {
                Socket socket = serverSocket.accept();
                ClientRequestHandle handle = new ClientRequestHandle(socket);
                handle.run();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        FlowServer server = new FlowServer();
        new Thread(server).start();
    }
}
