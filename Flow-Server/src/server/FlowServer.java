package server;

import database.SQLDatabase;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.logging.Logger;

public class FlowServer implements Runnable {

    private static Logger L = Logger.getLogger("FlowServer");
    public static String ERROR = "INTERNAL_SERVER_ERROR";
    public static final int PORT = 10244;
    // private static final int ARC_PORT = 10225;

    private SQLDatabase database;

    private static final int MAX_THREADS = 100;
    private Thread[] threadPool = new Thread[MAX_THREADS];

    public FlowServer() {
        this.database = new SQLDatabase("data/FlowDatabse.db");
    }

    @Override
    public void run() {
        DataManagement.getInstance().init(new File("data"));
        try {
            L.info("started listening");
            ServerSocket serverSocket = new ServerSocket(PORT);

            while (serverSocket.isBound()) {
                Socket socket = serverSocket.accept();
                L.info("accepted connection from " + socket.getRemoteSocketAddress());
                int i = 0;
                do {
                    i %= MAX_THREADS;
                    if (threadPool[i] != null && !threadPool[i].isAlive())
                        threadPool[i--] = null;
                    ++i;
                } while (threadPool[i] != null);

                L.info("Request assigned worker thread " + i);
                Thread t = new Thread(new ClientRequestHandle(this, socket));
                t.start();
                threadPool[i] = t;
            }
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected UUID newSession(String username) {
        UUID sessionId = UUID.randomUUID();
        this.database.newSession(username, sessionId.toString());
        return sessionId;
    }

    protected SQLDatabase getDatabase() {
        return this.database;
    }

    public static void main(String[] args) throws IOException, KeyManagementException, NoSuchAlgorithmException {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%n");
        FlowServer server = new FlowServer();
        new Thread(server).start();
        // TEST CODE
    }
}
