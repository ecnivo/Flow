package server;

import java.io.IOException;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
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
			// this.socket.setSoTimeout(500);
			String username, password, status;
			UUID random;
			String[][] response;
			Data data = psocket.receive();

			L.info("receive: " + data.toString());
			Data returnData = new Data();
			switch (data.getType()) {
			case "login":
				username = data.get("username", String.class);
				password = data.get("password", String.class);
				try {
					if (this.database.userExists(username)) {
						if (this.server.getDatabase().authenticate(username,
								password)) {
							// Inform server new session was created (server
							// will
							// save session to database)
							UUID sessionID = this.server.newSession(username);
							returnData.put("status", "OK");
							returnData.put("session_id", sessionID);
						} else {
							returnData.put("status", "PASSWORD_INCORRECT");
						}
					} else {
						returnData.put("status", "USERNAME_DOES_NOT_EXIST");
					}
				} catch (DatabaseException e) {
					e.printStackTrace();
				}
				break;
			case "user":
				String userCmdType = data.get("user_type", String.class);
				switch (userCmdType) {
				case "REGISTER":
					returnData.put("status",
							this.database.addUser(
									data.get("username", String.class),
									data.get("password", String.class)));
					DataManagement.getInstance().addUser(new User(
							data.get("username"), data.get("password")));
					/*
					 * TODO we need to know if the username/password have
					 * invalid characters
					 */
					break;
				case "CLOSE_ACCOUNT":
					this.database
							.closeAccount(data.get("username", String.class));
					returnData.put("status",
							DataManagement.getInstance()
									.removeUser(data.get("username")) ? "OK"
											: FlowServer.ERROR);
					break;
				case "CHANGE_PASSWORD":
					this.database.changePassword(
							data.get("username", String.class),
							data.get("new_password", String.class));
					returnData.put("status", "OK");
					break;
				}
				break;
			case "list_projects":
				// Initialized as null to prevent errors
				ResultSet temp = null;
				response = null;
				try {
					temp = this.database.getSessionInfo(
							data.get("session_id", UUID.class).toString());
				} catch (DatabaseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				try {
					String unmamedb = temp.getString("Username");
					if (DataManagement.getInstance()
							.getUserByUsername(unmamedb) == null)
						throw new RuntimeException("User does not exist");
					ResultSet projects = this.database.getProjects(unmamedb);
					response = Results.toStringArray(
							new String[] { "ProjectID" }, projects);
				} catch (SQLException e) {
					e.printStackTrace();
					// TODO Auto-generated catch block
					returnData.put("status", FlowServer.ERROR);
					break;
				} catch (DatabaseException e) {
					e.printStackTrace();
					// TODO Auto-generated catch block
					returnData.put("status", e.getMessage());
					break;
				}
				UUID[] projects = new UUID[response.length];
				System.out.println(Arrays.toString(response));
				if (response == null || response[0] == null) {
					returnData.put("projects", new UUID[0]);
				} else {
					for (int i = 0; i < response.length; i++) {
						projects[i] = UUID.fromString(response[i][0]);
					}
					returnData.put("projects", projects);
				}
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
							results.getString("DocumentName")); // TODO this is
					// not how you
					// get a file!
					// constructors
					// are only
					// called on
					// file
					// creation!
					// TODO Format
					// the above
					// amazingly
					// formatted
					// comment
					// properly
					returnData.put("document", file);
					returnData.put("status", "ok");
				} catch (DatabaseException e) {
					e.printStackTrace();
					returnData.put("status", e.getMessage());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					returnData.put("status", FlowServer.ERROR);
				}
				break;
			case "file_checksum":
				break;
			case "request_project":
				try {
					// TODO Move back to one line after debugging
					FlowProject temp1 = this.server.getProject(
							data.get("project_uuid", UUID.class).toString());
					System.out
							.println(temp1 != null ? temp1.toString() : temp1);
					returnData.put("project", temp1);
				} catch (DatabaseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					returnData.put("status", FlowServer.ERROR);
				}
				break;
			case "request_file":
				try {
					returnData.put("document", this.server.getFile(
							data.get("file_uuid", UUID.class).toString(),
							data.get("project_uuid", UUID.class).toString()));
				} catch (DatabaseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					returnData.put("status", FlowServer.ERROR);
				}
				break;
			case "new_project":
				try {
					String projectName = data.get("project_name", String.class);
					ResultSet sessInfo = this.database.getSessionInfo(
							data.get("session_id", UUID.class).toString());
					username = Results.toStringArray(
							new String[] { "Username" }, sessInfo)[0][0];
					FlowProject fp = new FlowProject(projectName, DataManagement
							.getInstance().getUserByUsername(username));
					DataManagement.getInstance().addProjectToUser(username, fp);
					status = this.database.newProject(
							fp.getProjectUUID().toString(), projectName,
							username);
					if (status != null && status.equals("OK")) {
						returnData.put("status",
								this.database.updateAccess(SQLDatabase.OWNER,
										fp.getProjectUUID().toString(),
										username));
						returnData.put("project_uuid",
								fp.getProjectUUID().toString());
					} else {
						returnData.put("status", status);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					returnData.put("status", FlowServer.ERROR);
				} catch (DatabaseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					returnData.put("status", e.getMessage());
				}
				break;
			case "project_modify":
				String projectId = data.get("project_uuid", String.class);
				switch (data.get("project_modify_type", String.class)) {
				case "MODIFY_COLLABORATOR":
					username = data.get("username", String.class);
					returnData
							.put("status",
									this.database.updateAccess(
											(int) data.get("access_level",
													Byte.class),
											projectId, data.get("username",
													String.class)));
					break;
				case "RENAME_PROJECT":
					returnData.put("status", this.database.renameProject(
							projectId, data.get("new_name", String.class)));
					break;
				case "DELETE_PROJECT":
					returnData.put("status",
							this.database.deleteProject(projectId));
					break;
				}
				break;
			case "new_textdocument":
				// TODO Implement new_arbitrarydocument
				// TODO Actually create file @netdex
				random = UUID.randomUUID();
				status = this.database.newFile(random.toString(),
						data.get("document_name", String.class),
						data.get("project_uuid", UUID.class).toString(),
						data.get("directory_uuid", UUID.class).toString());
				if (status.equals("OK")) {
					returnData.put("document_uuid", random);
				}
				returnData.put("status", status);
				break;
			case "new_directory":
				status = this.database.newDirectory(
						data.get("directory_name", String.class),
						(random = UUID.randomUUID()).toString(),
						data.get("project_uuid", UUID.class).toString(),
						data.get("parent_directory_uuid", UUID.class)
								.toString());
				if (status.equals("OK")) {
					returnData.put("directory_uuid", random);
				}
				returnData.put("status", status);
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
			L.info("response: " + returnData.toString());
		} catch (IOException e) {
			L.warning("communication error: " + e.getMessage());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			L.warning("ClassnotFoundException error: " + e.getMessage());
			e.printStackTrace();
		} // catch (Exception e) {
			// // TODO REMOVE THIS and catch individual exceptions
			// L.severe("Internal Server Error: " + e.getMessage());
			// }

		try {
			socket.close();
		} catch (IOException e) {
			L.severe("failure to close socket");
		}
	}
}
