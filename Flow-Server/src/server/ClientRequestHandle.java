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
import struct.TextDocument;
import struct.User;
import util.DataModification;
import util.DatabaseException;
import util.Results;

public class ClientRequestHandle implements Runnable {

	private Socket socket;
	private DataSocket psocket;
	private FlowServer server;
	private SQLDatabase database;

	private static Logger L = Logger.getLogger("ClientRequestHandle");

	public ClientRequestHandle(FlowServer server, Socket socket)
			throws IOException {
		this.socket = socket;
		this.psocket = new DataSocket(socket);
		this.server = server;
		this.database = this.server.getDatabase();
	}

	@Override
	public void run() {
		try {
			// this.socket.setSoTimeout(500);
			Data data = psocket.receive();

			L.info("receive: " + data.toString());
			Data returnData = new Data();
			switch (data.getType()) {
			case "login":
				try {
					String username = data.get("username", String.class),
							password = data.get("password", String.class);
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
			case "end_session":
				// TODO Deregister all associated listeners
				// TODO Call whatever code NETDEX has for this
				this.database.removeSession(
						data.get("session_id", UUID.class).toString());
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
						String status = "OK", username = this.database
								.getUsername(data.get("session_id", UUID.class)
										.toString());
						if (!DataManagement.getInstance()
								.removeUser(username)) {
							returnData.put("status", "INTERNAL_SERVER_ERROR");
						} else {
							this.database.closeAccount(username);
							returnData.put("status", "OK");
						}
					} catch (DatabaseException e) {
						e.printStackTrace();
						returnData.put("status", e.getMessage());
					}
					break;
				case "CHANGE_PASSWORD":
					try {
						this.database.changePassword(this.database.getUsername(
								data.get("session_id", UUID.class).toString()),
								data.get("new_password", String.class));
						returnData.put("status", "OK");
					} catch (DatabaseException e) {
						e.printStackTrace();
						returnData.put("status", e.getMessage());
					}
					break;
				}
				break;
			case "list_projects":
				// Initialized as null to prevent errors
				try {
					ResultSet temp = this.database.getSessionInfo(
							data.get("session_id", UUID.class).toString());
					String username = temp.getString("Username");
					if (DataManagement.getInstance()
							.getUserByUsername(username) == null)
						throw new RuntimeException("User does not exist");
					ResultSet projects = this.database.getProjects(username);
					String[][] response = Results.toStringArray(
							new String[] { "ProjectID" }, projects);
					UUID[] projectUUIDs = new UUID[response.length];
					System.out.println(Arrays.toString(response));
					if (response == null || response[0] == null) {
						returnData.put("projects", new UUID[0]);
					} else {
						for (int i = 0; i < response.length; i++) {
							projectUUIDs[i] = UUID.fromString(response[i][0]);
						}
						returnData.put("projects", projectUUIDs);
					}
					returnData.put("status", "OK");
				} catch (DatabaseException e) {
					e.printStackTrace();
					returnData.put("status", e.getMessage());
				} catch (SQLException e) {
					e.printStackTrace();
					returnData.put("status", FlowServer.ERROR);
				}
				break;
			case "new_project":
				try {
					String projectName = data.get("project_name", String.class);
					String username = this.database.getUsername(
							data.get("session_id", UUID.class).toString());
					UUID uuid = UUID.randomUUID();
					String status = this.database.newProject(uuid.toString(),
							projectName, username);
					if (status != null && status.equals("OK")) {
						returnData.put("status", this.database.updateAccess(
								SQLDatabase.OWNER, uuid.toString(), username));
						returnData.put("project_uuid", uuid);
					} else {
						returnData.put("status", status);
					}
				} catch (DatabaseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					returnData.put("status", e.getMessage());
				}
				break;
			case "new_textdocument": {
				UUID projectUUID = data.get("project_uuid", UUID.class);
				UUID directoryUUID = data.get("directory_uuid", UUID.class);
				String documentName = data.get("document_name", String.class);
				UUID fileUUID = UUID.randomUUID();
				UUID versionUUID = UUID.randomUUID();
				// TODO add the version to the database
				this.database.newFile(fileUUID.toString(), documentName,
						projectUUID.toString(), directoryUUID.toString(),
						"TEXT_DOCUMENT");
				TextDocument newTextDocument = new TextDocument();
				DataManagement.getInstance().addTextDocumentVersion(fileUUID,
						versionUUID, newTextDocument);
			}
				break;
			case "new_directory": {
				UUID projectUUID = data.get("project_uuid", UUID.class);
				UUID parentDirectoryUUID = data.get("parent_directory_uuid",
						UUID.class);
				UUID random = UUID.randomUUID();
				String status = this.database.newDirectory(
						data.get("directory_name", String.class),
						random.toString(), projectUUID.toString(),
						parentDirectoryUUID.toString());
				returnData.put("status", status);
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
						sessionInfo.next();
						String username = sessionInfo.getString("Username");
						returnData.put("status",
								this.database.updateAccess(
										(int) data.get("access_level",
												Byte.class),
										projectId, username));
					} catch (DatabaseException e) {
						e.printStackTrace();
						returnData.put("status", e.getMessage());
					} catch (SQLException e) {
						e.printStackTrace();
						returnData.put("status", FlowServer.ERROR);
					}
					break;
				case "RENAME_PROJECT":
					String newName = data.get("new_name", String.class);
					UUID projectUUID = data.get("project_uuid", UUID.class);
					returnData.put("status", this.database
							.renameProject(projectUUID.toString(), newName));
					break;
				case "DELETE_PROJECT":
					returnData.put("status",
							this.database.deleteProject(projectId));
					break;
				}
				break;
			case "directory_modify": {
				UUID directoryUUID = data.get("directory_uuid", UUID.class);
				String type = data.get("mod_type", String.class);
				switch (type) {
				case "RENAME":
					String newName = data.get("new_name", String.class);
					returnData.put("status", this.database.renameDirectory(
							directoryUUID.toString(), newName));
					break;
				case "DELETE":
					returnData.put("status", this.database
							.deleteDirectory(directoryUUID.toString()));
					break;
				}
			}
				break;
			case "file_modify": {
				UUID fileUUID = data.get("file_uuid", UUID.class);
				String modType = data.get("mod_type", String.class);
				try {
					String fileType = this.database
							.getFileType(fileUUID.toString());
					switch (modType) {
					case "RENAME":
						// TODO rename in Bimesh's code
						break;
					case "DELETE":
						// TODO delete in Bimesh's code
						DataManagement.getInstance().removeFileByUUID(fileUUID);
						break;
					}
				} catch (DatabaseException e) {
					e.printStackTrace();
					L.severe(e.getMessage());
					returnData.put("status", FlowServer.ERROR);
				}
			}
				break;
			case "project_info":
				try {
					UUID projectUUID = data.get("project_uuid", UUID.class);
					ResultSet databaseResponse = this.database
							.getProjectInfo(projectUUID.toString());
					returnData.put("project_name",
							databaseResponse.getString("ProjectName"));
					returnData.put("editors", this.database.getUsers(
							projectUUID.toString(), SQLDatabase.EDIT));
					returnData.put("viewers", this.database.getUsers(
							projectUUID.toString(), SQLDatabase.VIEW));
					returnData.put("owner", this.database.getUsers(
							projectUUID.toString(), SQLDatabase.OWNER));
					returnData.put("status", "OK");
				} catch (DatabaseException e) {
					e.printStackTrace();
					returnData.put("status", e.getMessage());
				} catch (SQLException e) {
					e.printStackTrace();
					returnData.put("status", FlowServer.ERROR);
				}
				break;
			case "directory_info":
				try {
					// Load the required data from the data packet
					UUID directoryUUID = data.get("directory_uuid", UUID.class);

					// Add information from the Directories table
					ResultSet results = this.database
							.getDirectoryInfo(directoryUUID.toString());
					returnData.put("parent_directory_uuid",
							results.getString("ParentDirectoryID"));
					returnData.put("directory_name",
							results.getString("DirectoryName"));

					// Add information from all documents located in the
					// specified directory
					results = this.database
							.getFilesInDirectory(directoryUUID.toString());
					returnData.put("child_files",
							DataModification.getUUIDsFromArray(Results
									.toStringArray("DocumentID", results)));

					// Add information from all sub directories located inside
					// the specified directory
					results = this.database.getDirectoriesInDirectory(
							directoryUUID.toString());
					returnData.put("child_directories",
							DataModification.getUUIDsFromArray(Results
									.toStringArray("DirectoryID", results)));

					// If no exceptions were thrown up to this point, no errors
					// occurred in the data retrieval.
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
				/*
				 * Retrieves all associated information
				 */
				try {
					// Load the required data from the data packet
					UUID fileUUID = data.get("file_uuid", UUID.class);

					// Add information from the Documents table
					ResultSet results = this.database
							.getFileInfo(fileUUID.toString());
					returnData.put("file_name",
							results.getString("DocumentName"));
					returnData.put("file_type",
							results.getString("DocumentType"));
					returnData.put("file_versions",
							DataModification.getUUIDsFromArray(this.database
									.getFileVersions(fileUUID.toString())));
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
				break;
			case "request_version": {
				UUID fileUUID = data.get("file_uuid", UUID.class);
				UUID versionUUID = data.get("version_uuid", UUID.class);
				byte[] bytes = null;
				try {
					String fileType = this.database
							.getFileType(fileUUID.toString());
					if (fileType.equals(SQLDatabase.TEXT_DOCUMENT)) {
						TextDocument doc = DataManagement.getInstance()
								.getTextDocument(fileUUID, versionUUID);
						bytes = doc.getDocumentText().getBytes();
					} else {
						bytes = DataManagement.getInstance()
								.getArbitraryFileBytes(fileUUID, versionUUID);
					}
					data.put("file_data", bytes);
					data.put("status", "OK");
				} catch (DatabaseException e) {
					e.printStackTrace();
					data.put("status", e.getMessage());
				}
			}
				break;
			case "document_request":
				try {
					UUID fileUUID = data.get("file_uuid", UUID.class);
					UUID versionUUID = UUID.fromString(this.database
							.getLatestVersionUUID(fileUUID.toString()));

					returnData.put("version_uuid", versionUUID);
					byte[] bytes = null;

					String fileType = this.database
							.getFileType(fileUUID.toString());
					if (fileType.equals(SQLDatabase.TEXT_DOCUMENT)) {
						TextDocument doc = DataManagement.getInstance()
								.getTextDocument(fileUUID, versionUUID);
						bytes = doc.getDocumentText().getBytes();
					} else {
						bytes = DataManagement.getInstance()
								.getArbitraryFileBytes(fileUUID, versionUUID);
					}
					data.put("file_data", bytes);
					data.put("status", "OK");
				} catch (DatabaseException e) {
					e.printStackTrace();
					returnData.put("status", e.getMessage());
				}
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
