package server;

import java.io.IOException;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Logger;

import message.Data;
import network.DataSocket;
import struct.User;
import struct.VersionText;
import util.DataManipulation;
import util.DatabaseException;
import util.Results;
import util.Validator;
import callback.DocumentCallbackEvent;
import callback.PersistentHandleManager;
import database.SQLDatabase;

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
			this.socket.setSoTimeout(500);
			final Data data = psocket.receive();

			L.info("receive: " + data.toString());
			final Data returnData = new Data();
			switch (data.getType()) {
			case "login":
				try {
					final String username = data.get("username", String.class), password = data
							.get("password", String.class);
					if (this.database.userExists(username)) {
						if (this.server.getDatabase().authenticate(username,
								password)) {
							try {
								UUID sessionID = this.server
										.newSession(username);
								returnData.put("session_id", sessionID);
								returnData.put("status", "OK");
							} catch (DatabaseException e) {
								e.printStackTrace();
								returnData.put("status", e.getMessage());
							}
						} else {
							returnData.put("status", "PASSWORD_INCORRECT");
						}
					} else {
						returnData.put("status", "USERNAME_DOES_NOT_EXIST");
					}
				} catch (DatabaseException e) {
					e.printStackTrace();
					returnData.put("status", e.getMessage());
				}
				break;
			case "end_session":
				// TODO Deregister all associated listeners
				// TODO Call whatever code NETDEX has for this
				returnData.put(
						"status",
						this.database.removeSession(data.get("session_id",
								UUID.class).toString()));
				break;
			case "user":
				String userCmdType = data.get("user_type", String.class);
				switch (userCmdType) {
				case "REGISTER": {
					String username = data.get("username", String.class), password = data
							.get("password", String.class);
					if (!Validator.validUserName(username))
						returnData.put("status", "USERNAME_INVALID");
					else if (!Validator.validUserName(username))
						returnData.put("status", "PASSWORD_INVALID");
					else {
						returnData.put("status",
								this.database.addUser(username, password));
						DataManagement.getInstance().addUser(
								new User(data.get("username", String.class),
										data.get("password", String.class)));
					}
				}
					break;
				case "CLOSE_ACCOUNT":
					try {
						String username = this.database.getUsername(data.get(
								"session_id", UUID.class).toString());
						if (!DataManagement.getInstance().removeUser(username)) {
							returnData.put("status", FlowServer.ERROR);
						} else {
							returnData.put("status",
									this.database.closeAccount(username));
						}
					} catch (DatabaseException e) {
						e.printStackTrace();
						returnData.put("status", e.getMessage());
					}
					break;
				case "CHANGE_PASSWORD":
					try {
						returnData.put("status", this.database.changePassword(
								this.database.getUsername(data.get(
										"session_id", UUID.class).toString()),
								data.get("new_password", String.class)));
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
					ResultSet temp = this.database.getSessionInfo(data.get(
							"session_id", UUID.class).toString());
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
					String username = this.database.getUsername(data.get(
							"session_id", UUID.class).toString());
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
			case "new_text_file":
				try {
					UUID projectUUID = data.get("project_uuid", UUID.class), sessionID = data
							.get("session_id", UUID.class);
					if (this.database.verifyPermissions(sessionID.toString(),
							projectUUID.toString(), SQLDatabase.EDIT)) {
						UUID directoryUUID = data.get("directory_uuid",
								UUID.class), fileUUID = UUID.randomUUID(), versionUUID = UUID
								.randomUUID();
						String documentName = data.get("file_name",
								String.class);
						if (Validator.validFileName(documentName)) {
							// TODO add the version to the database
							String status = this.database.newFile(
									fileUUID.toString(), documentName,
									projectUUID.toString(),
									directoryUUID.toString(), "TEXT_DOCUMENT");
							if (status.equals("OK")) {
								this.database.newVersion(fileUUID.toString(),
										versionUUID.toString());
								VersionText newTextDocument = new VersionText();
								VersionManager.getInstance().addTextVersion(
										fileUUID, versionUUID, newTextDocument);
								DataManagement.getInstance().flushTextToDisk(
										fileUUID, versionUUID, newTextDocument);
								returnData.put("file_uuid", fileUUID);
							}
							returnData.put("status", status);
						} else {
							returnData.put("status", "INVALID_FILE_NAME");
						}
					} else {
						returnData.put("status", "ACCESS_DENIED");
					}
				} catch (DatabaseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					returnData.put("status", FlowServer.ERROR);
				}
				break;
			case "new_directory":
				try {
					UUID projectUUID = data.get("project_uuid", UUID.class);
					UUID parentDirectoryUUID = data.get(
							"parent_directory_uuid", UUID.class);
					UUID sessionID = data.get("session_id", UUID.class);
					if (this.database.verifyPermissions(sessionID.toString(),
							projectUUID.toString(), SQLDatabase.EDIT)) {
						UUID random = UUID.randomUUID();
						String status = this.database.newDirectory(
								data.get("directory_name", String.class),
								random.toString(), projectUUID.toString(),
								parentDirectoryUUID.toString());
						if (status.equals("OK")) {
							returnData.put("directory_uuid", random);
						}
						returnData.put("status", status);
					} else {
						returnData.put("status", "ACCESS_DENIED");
					}
				} catch (DatabaseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					returnData.put("status", FlowServer.ERROR);
				}
				break;
			case "project_modify": {
				UUID projectUUID = data.get("project_uuid", UUID.class), sessionID = data
						.get("session_id", UUID.class);
				try {
					switch (data.get("project_modify_type", String.class)) {
					case "MODIFY_COLLABORATOR":
						String username = data.get("username", String.class);
						int accessLevel = (int) data.get("access_level",
								Byte.class);
						if (this.database.verifyPermissions(
								sessionID.toString(), projectUUID.toString(),
								SQLDatabase.OWNER)) {
							returnData.put("status", this.database
									.updateAccess(accessLevel,
											projectUUID.toString(), username));
						} else if (this.database.verifyPermissions(
								sessionID.toString(), projectUUID.toString(),
								SQLDatabase.EDIT)) {
							returnData.put("status", this.database
									.restrictedUpdateAccess(accessLevel,
											projectUUID.toString(), username));
						} else {
							returnData.put("status", "ACCESS_DENIED");
						}
						break;
					case "RENAME_PROJECT": {
						String newName = data.get("new_name", String.class);
						returnData.put(
								"status",
								this.database.renameProject(
										projectUUID.toString(), newName));
					}
						break;
					case "DELETE_PROJECT":
						if (this.database.verifyPermissions(
								sessionID.toString(), projectUUID.toString(),
								SQLDatabase.OWNER)) {
							returnData.put("status", this.database
									.deleteProject(projectUUID.toString()));
						} else {
							returnData.put("status", "ACCESS_DENIED");
						}
						break;
					}
				} catch (DatabaseException e) {
					e.printStackTrace();
					returnData.put("status", e.getMessage());
				}
			}
				break;
			case "directory_modify": {
				try {
					UUID directoryUUID = data.get("directory_uuid", UUID.class);
					String sessionID = data.get("session_id", UUID.class)
							.toString(), projectUUID = this.database
							.getProjectUUIDFromDirectory(directoryUUID
									.toString());

					if (this.database.verifyPermissions(sessionID, projectUUID,
							SQLDatabase.EDIT)) {
						String type = data.get("mod_type", String.class);
						switch (type) {
						case "RENAME":
							String newName = data.get("new_name", String.class);
							returnData.put(
									"status",
									this.database.renameDirectory(
											directoryUUID.toString(), newName));
							break;
						case "DELETE":
							returnData.put("status", this.database
									.deleteDirectory(directoryUUID.toString()));
							break;
						}
					} else {
						returnData.put("status", "ACCESS_DENIED");
					}
				} catch (DatabaseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					returnData.put("status", e.getMessage());
				}
			}
				break;
			case "file_metadata_modify":
				try {
					UUID fileUUID = data.get("file_uuid", UUID.class);
					String sessionID = data.get("session_id", UUID.class)
							.toString(), projectUUID = this.database
							.getProjectUUIDFromFile(fileUUID.toString());
					if (this.database.verifyPermissions(sessionID, projectUUID,
							SQLDatabase.EDIT)) {
						String modType = data.get("mod_type", String.class);
						switch (modType) {
						case "RENAME":
							returnData
									.put("status", this.database.renameFile(
											fileUUID.toString(),
											data.get("new_name", String.class)));
							break;
						case "DELETE":
							DataManagement.getInstance().removeFileByUUID(
									fileUUID);
							returnData.put("status", this.database
									.deleteFile(fileUUID.toString()));
							break;
						}
					} else {
						returnData.put("status", "ACCESS_DENIED");
					}
				} catch (DatabaseException e) {
					e.printStackTrace();
					L.severe(e.getMessage());
					returnData.put("status", FlowServer.ERROR);
				}
				break;
			case "project_info":
				try {
					UUID projectUUID = data.get("project_uuid", UUID.class);
					String sessionID = data.get("session_id", UUID.class)
							.toString();

					if (this.database.verifyPermissions(sessionID,
							projectUUID.toString())) {
						ResultSet databaseResponse = this.database
								.getProjectInfo(projectUUID.toString());
						returnData.put("project_name",
								databaseResponse.getString("ProjectName"));
						returnData.put("editors", this.database.getUsers(
								projectUUID.toString(), SQLDatabase.EDIT));
						returnData.put("viewers", this.database.getUsers(
								projectUUID.toString(), SQLDatabase.VIEW));
						returnData.put("owner", this.database.getUsers(
								projectUUID.toString(), SQLDatabase.OWNER)[0]);
						returnData.put("status", "OK");
					} else {
						returnData.put("status", "ACCESS_DENIED");
					}
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

					String sessionID = data.get("session_id", UUID.class)
							.toString(), projectUUID = this.database
							.getProjectUUIDFromDirectory(directoryUUID
									.toString());

					// Verify is the user has at least view access
					if (this.database.verifyPermissions(sessionID, projectUUID)) {
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
						returnData.put("child_files", DataManipulation
								.getUUIDsFromArray(Results.toStringArray(
										"DocumentID", results)));

						// Add information from all sub directories located
						// inside
						// the specified directory
						results = this.database
								.getDirectoriesInDirectory(directoryUUID
										.toString());
						returnData.put("child_directories", DataManipulation
								.getUUIDsFromArray(Results.toStringArray(
										"DirectoryID", results)));

						// If no exceptions were thrown up to this point, no
						// errors
						// occurred in the data retrieval.
						returnData.put("status", "OK");
					} else {
						returnData.put("status", "ACCESS_DENIED");
					}
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
					// Load the required data from the data packet
					String fileUUID = data.get("file_uuid", UUID.class)
							.toString(), sessionID = data.get("session_id",
							UUID.class).toString(), projectUUID = this.database
							.getProjectUUIDFromFile(fileUUID);

					// Verify is the user has at least view access
					if (this.database.verifyPermissions(sessionID, projectUUID)) {
						// Add information from the Documents table
						ResultSet results = this.database.getFileInfo(fileUUID);
						returnData.put("file_name",
								results.getString("DocumentName"));
						returnData.put("file_type",
								results.getString("FileType"));
						returnData.put("file_versions", DataManipulation
								.getUUIDsFromArray(this.database
										.getFileVersions(fileUUID)));
						returnData.put("status", "OK");
					} else {
						returnData.put("status", "ACCESS_DENIED");
					}
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
			case "version_info":
				try {
					String versionUUID = data.get("version_uuid", UUID.class)
							.toString(), sessionID = data.get("session_id",
							UUID.class).toString(), projectUUID = this.database
							.getProjectUUIDFromVersion(versionUUID);

					if (this.database.verifyPermissions(sessionID, projectUUID)) {
						returnData.put("date",
								this.database.getVersionDate(versionUUID));
						returnData.put("status", "OK");
					} else {
						returnData.put("status", "ACCESS_DENIED");
					}
				} catch (DatabaseException e) {
					e.printStackTrace();
					returnData.put("status", e.getMessage());
				}

			case "request_version":
				try {
					UUID fileUUID = data.get("file_uuid", UUID.class), versionUUID = data
							.get("version_uuid", UUID.class);
					String sessionID = data.get("session_id", UUID.class)
							.toString(), projectUUID = this.database
							.getProjectUUIDFromFile(fileUUID.toString());
					if (this.database.verifyPermissions(sessionID, projectUUID)) {
						byte[] bytes = null;
						String fileType = this.database.getFileType(fileUUID
								.toString());
						if (fileType.equals(SQLDatabase.TEXT_DOCUMENT)) {
							VersionText doc = VersionManager.getInstance()
									.getTextByVersionUUID(versionUUID);
							bytes = doc.getDocumentText().getBytes();
						} else {
							bytes = DataManagement.getInstance()
									.getArbitraryFileFromFile(fileUUID,
											versionUUID);
						}
						returnData.put("file_data", bytes);
						returnData.put("status", "OK");
					} else {
						returnData.put("status", "ACCESS_DENIED");
					}
				} catch (DatabaseException e) {
					e.printStackTrace();
					returnData.put("status", e.getMessage());
				}

				break;
			case "file_request":
				try {
					UUID fileUUID = data.get("file_uuid", UUID.class);
					String sessionID = data.get("session_id", UUID.class)
							.toString(), projectUUID = this.database
							.getProjectUUIDFromFile(fileUUID.toString());
					if (this.database.verifyPermissions(sessionID, projectUUID)) {
						UUID versionUUID = UUID.fromString(this.database
								.getLatestVersionUUID(fileUUID.toString()));
						returnData.put("version_uuid", versionUUID);
						byte[] bytes = null;
						String fileType = this.database.getFileType(fileUUID
								.toString());
						if (fileType.equals(SQLDatabase.TEXT_DOCUMENT)) {
							VersionText doc = VersionManager.getInstance()
									.getTextByVersionUUID(versionUUID);
							bytes = doc.getDocumentText().getBytes();
						} else {
							bytes = DataManagement.getInstance()
									.getArbitraryFileFromFile(fileUUID,
											versionUUID);
						}
						returnData.put("status", "OK");
						returnData.put("file_data", bytes);
					}
				} catch (DatabaseException e) {
					e.printStackTrace();
					returnData.put("status", e.getMessage());
				}
				break;
			case "file_text_modify":
				try {
					UUID fileUUID = data.get("file_uuid", UUID.class);
					int idx = data.get("idx", Integer.class);
					String username = this.database.getUsername(data.get(
							"session_id", UUID.class).toString());
					UUID latestVersionUUID = UUID.fromString(this.database
							.getLatestVersionUUID(fileUUID.toString()));
					VersionText td = VersionManager.getInstance()
							.getTextByVersionUUID(latestVersionUUID);
					switch (data.get("mod_type", String.class)) {
					case "INSERT": {
						String str = data.get("str", String.class);
						DocumentCallbackEvent event = new DocumentCallbackEvent(
								DocumentCallbackEvent.DocumentCallbackType.INSERT,
								fileUUID, username, idx, str, -1);
						PersistentHandleManager.getInstance().doCallbackEvent(
								fileUUID, event);
						for (char c : str.toCharArray()) {
							// TODO GORDON STOP CHANGING THIS TO --
							td.insert(c, idx++);
						}

					}
						break;
					case "DELETE": {
						int len = data.get("len", Integer.class);
						DocumentCallbackEvent event = new DocumentCallbackEvent(
								DocumentCallbackEvent.DocumentCallbackType.DELETE,
								fileUUID, username, idx, null, len);
						PersistentHandleManager.getInstance().doCallbackEvent(
								fileUUID, event);
						while (len-- > 0)
							td.delete(idx);
					}
					case "MOVE": {
						DocumentCallbackEvent event = new DocumentCallbackEvent(
								DocumentCallbackEvent.DocumentCallbackType.MOVE,
								fileUUID, username, idx, null, -1);
						PersistentHandleManager.getInstance().doCallbackEvent(
								fileUUID, event);
					}
						break;
					}
					returnData.put("status", "OK");
				} catch (DatabaseException e) {
					e.printStackTrace();
					returnData.put("status", e.getMessage());
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
		} catch (Exception e) {
			L.severe("Internal Server Error: " + e.getMessage());
			e.printStackTrace();
			Data data = new Data();
			data.put("status", FlowServer.ERROR);
			try {
				psocket.send(data);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		try {
			socket.close();
		} catch (IOException e) {
			L.severe("failure to close socket");
		}
	}
}
