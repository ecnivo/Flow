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
import util.DataModification;
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
			String username = null, password = null, status = null;
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
					try {
						status = "OK";
						username = this.database.getUsername(
								data.get("session_id", UUID.class).toString());
						if (!DataManagement.getInstance()
								.removeUser(username)) {
							status = "INTERNAL_SERVER_ERROR";
						}
						this.database.closeAccount(username);
					} catch (DatabaseException e) {
						e.printStackTrace();
						status = e.getMessage();
					}
					returnData.put("status", status);
					break;
				case "CHANGE_PASSWORD":
					try {
						this.database.changePassword(this.database.getUsername(
								data.get("session_id", UUID.class).toString()),
								data.get("new_password", String.class));
						status = "OK";
					} catch (DatabaseException e) {
						e.printStackTrace();
						status = e.getMessage();
					}
					returnData.put("status", status);
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
					username = temp.getString("Username");
					if (DataManagement.getInstance()
							.getUserByUsername(username) == null)
						throw new RuntimeException("User does not exist");
					ResultSet projects = this.database.getProjects(username);
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
					ResultSet results = this.database.getFileInfo(
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
				// TODO DECIDE BETWEEN file_request (above) and request_file
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
					FlowProject project = new FlowProject(projectName,
							DataManagement.getInstance()
									.getUserByUsername(username));
					DataManagement.getInstance().addProjectToUser(project);
					status = this.database.newProject(
							project.getProjectUUID().toString(), projectName,
							username);
					if (status != null && status.equals("OK")) {
						returnData.put("status",
								this.database.updateAccess(SQLDatabase.OWNER,
										project.getProjectUUID().toString(),
										username));
						// returnData.put("project", project);
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
				String projectId = data.get("project_uuid", UUID.class)
						.toString();
				switch (data.get("project_modify_type", String.class)) {
				case "MODIFY_COLLABORATOR":
					ResultSet sessionInfo = null;
					try {
						sessionInfo = this.database
								.getSessionInfo(data.get("session_id"));

					} catch (DatabaseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						sessionInfo.next();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					try {
						username = sessionInfo.getString("Username");
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					returnData
							.put("status",
									this.database.updateAccess(
											(int) data.get("access_level",
													Byte.class),
											projectId, username));
					break;
				case "RENAME_PROJECT":
					String newName = data.get("new_name", String.class);
					if (!DataManagement.getInstance().renameProject(
							UUID.fromString(projectId), newName)) {
						L.warning("Could not rename project from file system!");
					}
					returnData.put("status",
							this.database.renameProject(projectId, newName));
					break;
				case "DELETE_PROJECT":
					returnData.put("status",
							this.database.deleteProject(projectId));
					if (!DataManagement.getInstance()
							.removeProject(UUID.fromString(projectId))) {
						L.warning("Could not delete project from file system!");
					}
					break;
				}
				break;
			case "new_textdocument":
				// // TODO Implement new_arbitrarydocument
				// // TODO Actually create file @netdex
				// random = UUID.randomUUID();
				// status = this.database.newFile(random.toString(),
				// data.get("document_name", String.class),
				// data.get("project_uuid", UUID.class).toString(),
				// data.get("directory_uuid", UUID.class).toString());
				// // if (status.equals("OK")) {
				// // returnData.put("document_uuid", random);
				// // }
				// if (status.equals("OK")) {
				// try {
				// DataManagement.getInstance().addTextDocumentToProject(
				// data.get("project_uuid", UUID.class),
				// new TextDocument(
				// new FlowFile(
				// DataManagement.getInstance()
				// .getFolderFromPath(
				// data.get(
				// "project_uuid",
				// UUID.class),
				// DataModification
				// .getDirectoryPath(
				// "directory_uuid")),
				// data.get("document_name",
				// String.class),
				// random),
				// random, new Date()));
				// } catch (DatabaseException e) {
				// status = e.getMessage();
				// }
				// }
				returnData.put("status", status);
				break;
			case "new_directory":
				// status = this.database.newDirectory(
				// data.get("directory_name", String.class),
				// (random = UUID.randomUUID()).toString(),
				// data.get("project_uuid", UUID.class).toString(),
				// data.get("parent_directory_uuid", UUID.class)
				// .toString());
				// // if (status.equals("OK")) {
				// // returnData.put("directory_uuid", random);
				// // }
				// if (status.equals("OK")) {
				// UUID projectUUID = data.get("project_uuid", UUID.class);
				// UUID parentDirectoryUUID = data.get("parent_directory_uuid",
				// UUID.class);
				// FlowDirectory parentDirectory =
				// DataManagement.getInstance().getFolderFromPath(projectUUID,
				// "path");
				// String directoryName = data.get("directory_name",
				// String.class);
				// FlowDirectory flowDirectory = new
				// FlowDirectory(directoryName, random);
				// DataManagement.getInstance().createFolderInProject(projectUUID,
				// flowDirectory);
				// }
				// returnData.put("status", status);
				break;
			case "directory_info":
				try {
					UUID projectUUID = data.get("project_uuid"),
							directoryUUID = data.get("directory_uuid");
					ResultSet results = this.database
							.getDirectoryInfo(directoryUUID.toString());
					returnData.put("parent_directory_uuid",
							results.getString("ParentDirectoryID"));
					returnData.put("directory_name",
							results.getString("DirectoryName"));
					results = this.database.getFiles(projectUUID.toString(),
							directoryUUID.toString());
					returnData.put("child_files",
							DataModification.getUUIDsFromArray(Results
									.toStringArray("DocumentID", results)));
					results = this.database.getDirectories(
							projectUUID.toString(), directoryUUID.toString());
					returnData.put("child_directories",
							DataModification.getUUIDsFromArray(Results
									.toStringArray("DirectoryID", results)));
					returnData.put("status", "OK");
				} catch (DatabaseException e) {
					e.printStackTrace();
					L.severe(e.getMessage());
					data.put("status", FlowServer.ERROR);
				} catch (SQLException e) {
					e.printStackTrace();
					L.severe(e.getMessage());
					data.put("status", e.getMessage());
				}
				break;
			case "file_info":
				try {
					UUID projectUUID = data.get("project_uuid"),
							fileUUID = data.get("file_uuid");
					ResultSet results = this.database
							.getFileInfo(fileUUID.toString());
					returnData.put("file_name",
							results.getString("DocumentName"));
					returnData.put("file_type",
							results.getString("DocumentType"));
					returnData.put("status", "OK");
				} catch (DatabaseException e) {
					e.printStackTrace();
					L.severe(e.getMessage());
					returnData.put("status", e.getMessage());
				} catch (SQLException e) {
					e.printStackTrace();
					L.severe(e.getMessage());
					returnData.put("status", FlowServer.ERROR);
				}
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
			case "end_session":
				// TODO Deregister all associated listeners
				// TODO Call whatever code NETDEX has for this
				this.database.removeSession(
						data.get("session_id", UUID.class).toString());
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
			L.warning("ClassNotFoundException error: " + e.getMessage());
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
