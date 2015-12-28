package server;

import database.SQLDatabase;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

public class FlowServer implements Runnable {

    private static final int PORT = 10244;

    private SQLDatabase database;

    private static final int MAX_THREADS = 100;
    private Thread[] threadPool = new Thread[MAX_THREADS];

    public FlowServer() {

    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (serverSocket.isBound()) {
                Socket socket = serverSocket.accept();

                int i;
                do {
                    for (i = 0; i < MAX_THREADS && threadPool[i] != null; i++)
                        if (!threadPool[i].isAlive())
                            threadPool[i] = null;
                } while (threadPool[i] != null);

                System.err.println("Request assigned worker thread " + i);
                Thread t = new Thread(new ClientRequestHandle(socket));
                t.start();
                threadPool[i] = t;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        FlowServer server = new FlowServer();
        new Thread(server).start();
    }

    protected UUID newSession() {
        return UUID.randomUUID();
    }

    protected SQLDatabase getDatabase() {
        return this.database;
    }
}
