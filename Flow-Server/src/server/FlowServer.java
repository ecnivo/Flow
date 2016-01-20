package server;

import database.SQLDatabase;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main container for initiating database and dynamically dispatching clients to
 * new threads containing ClientRequestHandle runnable objects.
 * 
 * @version January 14th, 2016
 * @author Bimesh De Silva
 *
 */
public class FlowServer implements Runnable {

	/**
	 * Single instance of the FlowServer class
	 */
	private static FlowServer instance;

	/**
	 * Common logger for the entire project
	 */
	private static final Logger LOGGER = Logger.getLogger("FLOW");

	/**
	 * Represents an error caused by the server, rather than client.
	 */
	public static final String ERROR = "INTERNAL_SERVER_ERROR";

	/**
	 * Port for all communication from client to server.
	 */
	public static final int PORT = 10244;

	/**
	 * Port for asynchronous communication to update clients of document
	 * changes.
	 */
	public static final int ARC_PORT = 10225;

	/**
	 * Maximum amount of requests that can be handled at the same time.
	 */
	private static final int MAX_THREADS = 100;

	/**
	 * Array containing all active requests.
	 */
	private final Thread[] threadPool = new Thread[MAX_THREADS];

	/**
	 * Initiates the SQLDatabase class.
	 */
	private FlowServer() {
		SQLDatabase.getInstance();
	}

	/**
	 * Returns the latest instance of the FlowServer.
	 *
	 * @return the latest instance of the FlowServer, or a new FlowServer object
	 *         if one has yet to be initialized.
	 */
	private static FlowServer getInstance() {
		if (instance == null)
			instance = new FlowServer();
		return instance;
	}

	@Override
	public void run() {
		DataManagement.getInstance().init(new File("data"));
		try {
			LOGGER.info("started listening");
			ServerSocket serverSocket = new ServerSocket(PORT);
			serverSocket.setReceiveBufferSize(64000);
			serverSocket.setPerformancePreferences(1, 0, 0);

			while (serverSocket.isBound()) {
				Socket socket = serverSocket.accept();
				// Make the socket favor short and latent connections
				socket.setPerformancePreferences(1, 0, 0);
				socket.setTcpNoDelay(true);
				LOGGER.info("accepted connection from "
						+ socket.getRemoteSocketAddress());
				// Assign the client request a thread
				int i = 0;
				do {
					i %= MAX_THREADS;
					if (threadPool[i] != null && !threadPool[i].isAlive())
						threadPool[i--] = null;
					++i;
				} while (threadPool[i] != null);

				LOGGER.info("Request assigned worker thread " + i);
				Thread t = new Thread(new ClientRequestHandle(socket));
				t.start();
				threadPool[i] = t;
			}
			serverSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException,
			KeyManagementException, NoSuchAlgorithmException {
		LOGGER.setLevel(Level.ALL);
		FlowServer server = FlowServer.getInstance();
		new Thread(server).start();
		new Thread(new AsyncServer()).start();
	}
}
