package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ClientHandle implements Runnable {

	private Socket socket;

	private BufferedReader in;
	private PrintStream out;

	private User user;

	public ClientHandle(Socket socket) throws IOException {
		this.socket = socket;

		this.in = new BufferedReader(
				new InputStreamReader(socket.getInputStream()));
		this.out = new PrintStream(socket.getOutputStream());
	}

	@Override
	public void run() {

		while (socket.isConnected()) {

		}

	}

}
