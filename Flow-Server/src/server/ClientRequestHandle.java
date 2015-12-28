package server;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

import database.SQLDatabase;
import message.Data;
import network.DataSocket;
import struct.FlowProject;
import util.Results;

public class ClientRequestHandle implements Runnable {

	private Socket socket;
	private DataSocket psocket;
	private FlowServer server;
	private SQLDatabase database;

	private UUID uuid;

	public ClientRequestHandle(FlowServer server, Socket socket)
			throws IOException {
		this.socket = socket;
		this.psocket = new DataSocket(socket);
		this.uuid = UUID.randomUUID();
		this.server = server;
		this.database = this.server.getDatabase();
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
				} else {
					returnData.put("status", "INVALID_CREDENTIALS");
				}
				break;
			case "user":
				String userCmdType = data.get("user_type", String.class);
				switch (userCmdType) {
				case "REGISTER":
					this.database.addUser(data.get("username", String.class),
							data.get("password", String.class));
					break;
				case "CLOSE_ACCOUNT":
					break;
				case "CHANGE_PASSWORD":
					break;
				}
				break;
			case "list_projects":
				// TODO Acquire user UUID from session in database
				String[][] response = Results.toStringArray(new String[] {},
						this.database.getProjects("REPLACE WITH USERUUID"));
				FlowProject[] projects = new FlowProject[response.length];
				for (int i = 0; i < response.length; i++) {
					projects[i] = new FlowProject();
					// TODO Add data to flow project
				}
				returnData.put("projects", projects);
				break;
			case "list_project_files":
				break;
			case "file_request":
				break;
			case "file_checksum":
				break;
			case "new_project":
				break;
			case "project_modify":
				break;
			case "document_modify":
				break;
			}

			this.psocket.send(returnData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
