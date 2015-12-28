package server;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

import message.Data;
import network.DataSocket;

public class ClientRequestHandle implements Runnable {

	private Socket socket;
	private DataSocket psocket;
	private FlowServer server;

	private UUID uuid;

	public ClientRequestHandle(Socket socket) throws IOException {
		this.socket = socket;
		this.psocket = new DataSocket(socket);
		this.uuid = UUID.randomUUID();
	}

	@Override
	public void run() {
		try {
			this.socket.setSoTimeout(100);
			Data data = psocket.receive(Data.class);
			Data returnData = new Data();
			switch (data.getType()) {
			case "login":
				String username = data.get("username", String.class);
				String password = data.get("password", String.class);
				// TODO query the database
				if (this.server.getDatabase().authenticate(username,
						password)) {
					UUID sessionID = this.server.newSession();
					returnData.put("status", "OK");
					returnData.put("session_id", sessionID);
					// TODO write session ID into database
				} else {
					returnData.put("status", "INVALID_CREDENTIALS");
				}
				break;
			case "user":
				String userCmdType = data.get("user_type", String.class);
				switch (userCmdType) {
				case "REGISTER":
					break;
				case "CLOSE_ACCOUNT":
					break;
				case "CHANGE_PASSWORD":
					break;
				}
				break;
			}
			this.psocket.send(returnData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
