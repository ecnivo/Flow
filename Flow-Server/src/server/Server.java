package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.UUID;

import database.SQLDatabase;

public class Server {
	private SQLDatabase database;
	private ServerSocket socket;
	private boolean terminate;
	private ArrayList<ClientHandle> requests;

	public Server() {
		this.database = new SQLDatabase("flow-database");
		this.terminate = false;
		try {
			this.socket = new ServerSocket();
		} catch (IOException e) {
			System.err.println("Error initializing server socket");
			e.printStackTrace();
		}

		while (!this.terminate) {
			try {
				this.requests.add(new ClientHandle(this, this.socket.accept()));
			} catch (IOException e) {
				System.err.println("Error accepting new request");
				e.printStackTrace();
			}
		}

		// Forces all requests to be completed before terminating server
		while (this.requests.size() > 0) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				System.err.println("Server thread was interupted");
				e.printStackTrace();
			}
		}
	}

	protected void processedRequest(ClientHandle request) {
		this.requests.remove(request);
	}

	protected SQLDatabase getDatabase() {
		return this.database;
	}

	protected UUID newSession() {
		return UUID.randomUUID();
	}
}
