package server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;

import database.SQLDatabase;
import message.Data;
import network.FMLNetworker;

import javax.net.ssl.*;

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
				int i = 0;
				do {
					i %= MAX_THREADS;
					if (threadPool[i] != null && !threadPool[i].isAlive())
						threadPool[i--] = null;
					++i;
				} while (threadPool[i] != null);

				System.err.println("Request assigned worker thread " + i);
				Thread t = new Thread(new ClientRequestHandle(this, socket));
				t.start();
				threadPool[i] = t;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException, KeyManagementException, NoSuchAlgorithmException {
		FlowServer server = new FlowServer();
		new Thread(server).start();
		// TEST CODE



	}

	protected UUID newSession(String userId, String serialNumber) {
		UUID sessionId = UUID.randomUUID();
		this.database.newSession(userId, serialNumber, sessionId.toString());
		return sessionId;
	}

	protected SQLDatabase getDatabase() {
		return this.database;
	}
}
