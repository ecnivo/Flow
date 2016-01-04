package server;

import java.io.IOException;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.UUID;
import java.util.logging.Logger;

import database.SQLDatabase;
import message.Data;
import network.DataSocket;
import struct.FlowDirectory;
import struct.FlowFile;
import struct.FlowProject;
import struct.User;
import util.DatabaseException;
import util.Results;

public class ClientRequestHandle implements Runnable {

	private Socket socket;
	private DataSocket psocket;
	private FlowServer server;
	private SQLDatabase database;

	private UUID uuid;

	private static Logger L = Logger.getLogger("ClientRequestHandle");

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
			L.info(data.toString());
			L.info("received data from remote user");
			Data returnData = new Data();
			switch (data.getType()) {
			case "login":
				L.info("request for login message");
				username = data.get("username", String.class);
				password = data.get("password", String.class);
				if (this.server.getDatabase().authenticate(username,
						password)) {
					UUID sessionID = this.server.newSession(username);
					returnData.put("status", "OK");
					returnData.put("session_id", sessionID);
				} else {
					returnData.put("status", "INVALID_CREDENTIALS");
				}
				break;
			case "user":
				L.info("request for user message");
				String userCmdType = data.get("user_type", String.class);
				switch (userCmdType) {
				case "REGISTER":
					L.info("register user message");
					this.database.addUser(data.get("username", String.class), data.get("password", String.class));
					returnData.put("status", "OK");
					break;
				case "CLOSE_ACCOUNT":
					L.info("close account message");
					this.database
							.closeAccount(data.get("username", String.class));
					returnData.put("status", "OK");
					break;
				case "CHANGE_PASSWORD":
					L.info("change password message");
					this.database.changePassword(
							data.get("username", String.class),
							data.get("new_password", String.class));
					returnData.put("status", "OK");
					break;
				}
				break;
			case "list_projects":
				L.info("request for list projects");
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
				// TODO Verify if this is not needed and remove case
				// response = Results.toStringArray(
				// new String[] { "ProjectID", "ProjectName" },
				// this.database.getFiles(
				// username = data.get("username", String.class)));
				// FlowFile[] files = new FlowFile[response.length];
				// for (int i = 0; i < response.length; i++) {
				// files[i] = new FlowFile(new
				// FlowDirectory(this.database.getPath(data.get(key, type))),
				// "REPLACE WITH NAME",
				// UUID.fromString(response[i][0]));
				// }
				// returnData.put("files", files);
				// returnData.put("status", "OK");
				break;
			case "file_request":
				// TODO generate byte array using file path (from above)
				try {
					ResultSet results = this.database.getFile(
							data.get("doc_uuid", UUID.class).toString());
					FlowFile file = new FlowFile(
							new FlowDirectory(results.getString("Path")),
							results.getString("DocumentName"));
					returnData.put("document", file);
					returnData.put("status", "ok");
				} catch (DatabaseException e) {
					returnData.put("status", e.getMessage());
				}
				break;
			case "file_checksum":
				// TODO implement this
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
					try {
						this.database.updateAccess(
								(int) data.get("access_level", Byte.class),
								projectId, data.get("username", String.class));
					} catch (DatabaseException e) {
						e.printStackTrace();
						returnData.put("status", e.getMessage());
					}
					break;
				case "RENAME_PROJECT":
					try {
						this.database.renameProject(projectId,
								data.get("new_name", String.class));
					} catch (DatabaseException e) {
						e.printStackTrace();
						returnData.put("status", e.getMessage());
					}
					break;
				case "DELETE_PROJECT":
					try {
						this.database.deleteProject(projectId);
					} catch (DatabaseException e) {
						e.printStackTrace();
						returnData.put("status", e.getMessage());
					}
					break;
				}
				break;
			case "new_document":
				// TODO Implement checking if file is inside alternate directory
				this.database.newFile(data.get("file_name", String.class),
						data.get("project_uuid", UUID.class).toString());
				break;
			case "new_directory":
				// TODO Implement checking if directory is to be inside another
				// directory
				this.database.newDirectory(
						data.get("directory_name", String.class),
						data.get("project_uuid", UUID.class).toString());
				break;
			// TODO Implement sending messages to active sessions on changes
			// ^-- NETDEX
			case "document_modify":
				switch (data.get("doc_type", String.class)) {
				case "INSERT":
					break;
				case "DELETE":
					break;
				}
				break;
			default:
				// For completeness's sake
				returnData.put("status", "INVALID_REQUEST_TYPE");
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
