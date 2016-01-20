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

	private static FlowServer instance;

	private static Logger L = Logger.getLogger("FLOW");

	/**
	 * Represents an error caused by the server, rather than client.
	 */
	public static String ERROR = "INTERNAL_SERVER_ERROR";

	public static final int PORT = 10244;

	public static final int ARC_PORT = 10225;

	private static final int MAX_THREADS = 100;
	private Thread[] threadPool = new Thread[MAX_THREADS];

	private FlowServer() {
		// Load the SQLDatabase
		SQLDatabase.getInstance();
	}

	/**
	 * Returns the latest instance of the FlowServer.
	 *
	 * @return the latest instance of the FlowServer, or a new FlowServer object
	 *         if one has yet to be initialized.
	 */
	public static FlowServer getInstance() {
		if (instance == null)
			instance = new FlowServer();
		return instance;
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
				// Make the socket favor short and latent connections
				socket.setPerformancePreferences(1, 0, 0);
				socket.setTcpNoDelay(true);
				L.info("accepted connection from "
						+ socket.getRemoteSocketAddress());
				// Assign the client request a thread
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

	public static void main(String[] args) throws IOException,
			KeyManagementException, NoSuchAlgorithmException {
		//System.setProperty("java.util.logging.SimpleFormatter.format",
		//		"%4$s: %5$s%n");
		L.setLevel(Level.ALL);
		FlowServer server = FlowServer.getInstance();
		new Thread(server).start();
		new Thread(new AsyncServer(ARC_PORT)).start();
	}
}
