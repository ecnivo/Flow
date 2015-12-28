package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

import database.SQLDatabase;

public class FlowServer implements Runnable {

	private static final int PORT = 10244;

	private SQLDatabase database;

	public FlowServer() {

	}

	@Override
	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(PORT);
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

	protected UUID newSession() {
		return UUID.randomUUID();
	}

	protected SQLDatabase getDatabase() {
		return this.database;
	}
}
