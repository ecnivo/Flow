package server;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

import database.SQLDatabase;
import message.Data;
import network.DataSocket;
import struct.FlowFile;
import struct.FlowProject;
import struct.User;
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
			String username, password;
			String[][] response;
			Data data = psocket.receive(Data.class);
			System.err.println(data.toString());
			Data returnData = new Data();
			switch (data.getType()) {
			case "login":
				username = data.get("username", String.class);
				password = data.get("password", String.class);
				if (this.server.getDatabase().authenticate(username,
						password)) {
					// TODO Netdex get the serial number
					UUID sessionID = this.server.newSession(username,
							"REPLACE WITH SERIAL NUMBER");

					// TODO Add check for if the session cannot be created
					// This could potentially be in the authenticate method
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
				response = Results.toStringArray(
						new String[] { "ProjectID", "ProjectName" },
						this.database.getProjects(
								username = data.get("username", String.class)));
				FlowProject[] projects = new FlowProject[response.length];
				for (int i = 0; i < response.length; i++) {
					projects[i] = new FlowProject(response[i][1],
							new User(username),
							UUID.fromString(response[i][0]));
				}
				returnData.put("projects", projects);
				returnData.put("status", "OK");
				break;
			case "list_project_files":
				response = Results.toStringArray(
						new String[] { "ProjectID", "ProjectName" },
						this.database.getFiles(
								username = data.get("username", String.class)));
				FlowFile[] files = new FlowFile[response.length];

				// TODO Generate 'remotePath' and 'remoteName' <-- NETDEX what
				// does this even mean
				for (int i = 0; i < response.length; i++) {
					files[i] = new FlowFile("PLACE WITH PATH",
							"REPLACE WITH NAME",
							UUID.fromString(response[i][0]));
				}
				returnData.put("files", files);
				returnData.put("status", "OK");
				break;
			case "file_request":
				// TODO generate byte array using file path (from above)
				break;
			case "file_checksum":
				break;
			case "new_project":
				this.database
						.newProject(data.get("project_name", String.class),
								Results.toStringArray(
										new String[] { "OwnerUsername" },
										this.database.getSessionInfo(
												data.get("session_id",
														String.class)))[0][0]);
				break;
			case "project_modify":
				String projectId = data.get("project_uuid", String.class);
				switch (data.get("project_modify_type", String.class)) {
				case "MODIFY_COLLABORATOR":
					username = data.get("username", String.class);
					String accessLevel = data.get("access_level", String.class);
					switch (accessLevel) {
					case "NONE":
						// TODO create collaborator removal method in database
						break;
					case "VIEW":
						this.database.updateAccess(SQLDatabase.VIEW, projectId,
								username);
						break;
					case "EDIT":
						this.database.updateAccess(SQLDatabase.EDIT, projectId,
								username);
						break;
					}
					break;
				// TODO Create database methods for following functions
				case "RENAME_PROJECT":
					break;
				case "DELETE_PROJECT":
					break;
				}
				break;
			// TODO Implement sending messages to active sessions on changes
			case "document_modify":
				switch (data.get("doc_type", String.class)) {
				case "INSERT":
					break;
				case "DELETE":
					break;
				}
				break;
			}

			this.psocket.send(returnData);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				this.psocket.send(null);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}
}