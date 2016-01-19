package server;

import database.SQLDatabase;
import util.DatabaseException;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.logging.Logger;

public class FlowServer implements Runnable {

	public static FlowServer instance;

	public static FlowServer getInstance() {
		return instance;
	}

	private static Logger L = Logger.getLogger("FlowServer");

	/**
	 * Represents an error caused by the server, rather than client.
	 */
	public static String ERROR = "INTERNAL_SERVER_ERROR";

	public static final int PORT = 10244;

	public static final int ARC_PORT = 10225;

	private SQLDatabase database;

	// TODO Verify capability when running at max threads
	private static final int MAX_THREADS = 100;
	private Thread[] threadPool = new Thread[MAX_THREADS];

	public FlowServer() {
		this.database = new SQLDatabase();
		instance = this;
	}

	@Override
	public void run() {
		DataManagement.getInstance().init(new File("data"));
		try {
			L.info("started listening");
			ServerSocket serverSocket = new ServerSocket(PORT);
			serverSocket.setReceiveBufferSize(64000);
			serverSocket.setPerformancePreferences(1, 0, 0);

			while (serverSocket.isBound()) {
				Socket socket = serverSocket.accept();
				socket.setPerformancePreferences(1, 0, 0);
				socket.setTcpNoDelay(true);
				L.info("accepted connection from "
						+ socket.getRemoteSocketAddress());
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

	protected UUID newSession(String username) throws DatabaseException {
		UUID sessionId = UUID.randomUUID();
		this.database.newSession(username, sessionId.toString());
		return sessionId;
	}

	/**
	 * Getter for the associated SQLDatabse instance.
	 * 
	 * @return the associated SQLDatabase instance.
	 */
	public SQLDatabase getDatabase() {
		return this.database;
	}

	public static void main(String[] args) throws IOException,
			KeyManagementException, NoSuchAlgorithmException {
		System.setProperty("java.util.logging.SimpleFormatter.format",
				"%4$s: %5$s%n");
		FlowServer server = new FlowServer();
		new Thread(server).start();
		new Thread(new AsyncServer(ARC_PORT)).start();
		// TEST CODE
	}
}
