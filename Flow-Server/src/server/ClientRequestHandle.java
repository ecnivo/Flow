package server;

import callback.DocumentCallbackEvent;
import callback.PersistentHandleManager;
import database.SQLDatabase;
import message.Data;
import network.DataSocket;
import struct.User;
import struct.VersionText;
import util.DataManipulation;
import util.DatabaseException;
import util.Results;
import util.Validator;

import java.io.IOException;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Runnable that handles a single client request. Capable of dealing with a
 * variety of request types, the key categories being:
 * <ul>
 * <li>User accounts creation / modification</li>
 * <li>Creating / ending user sessions</li>
 * <li>Project meta data and permissions modification</li>
 * <li>Directory / file / version meta data modification</li>
 * <li>New project / directory / file creation</li>
 * <li>File / version data retrieval</li>
 * <li>File data modification</li>
 * </ul>
 * 
 * @version January 19th, 2016
 * @author Bimesh De Silva
 *
 */
class ClientRequestHandle implements Runnable {

	private final Socket socket;
	private DataSocket psocket;
	private SQLDatabase database;

	private static final Logger LOGGER = Logger.getLogger("FLOW");

	public ClientRequestHandle(Socket socket)
			throws IOException {
		this.socket = socket;
		this.psocket = new DataSocket(socket);
		this.database = SQLDatabase.getInstance();
	}

	@Override
	public void run() {
		try {

			// Prevent malicious / invalid requests from taking up too much
			// server time
			this.socket.setSoTimeout(500);

			// Retrieve the request from the client
			final Data data = psocket.receive();

			LOGGER.info("receive: " + data.toString());

			// Create a Data object to send back to the client
			final Data returnData = new Data();

			// Deal with each message type separately
			switch (data.getType()) {
			// For when the user doesn't have an active session open already
			case "login":
				try {
					final String username = data.get("username", String.class),
							password = data.get("password", String.class);

					// Verify data in request and send back appropriate error
					// message if needed, otherwise create a new session.
					if (this.database.userExists(username)) {
						if (this.database.authenticate(username, password)) {
							try {
								// Only provide a session id if all checks are
								// passed.
								UUID sessionID = UUID.randomUUID();
								if (this.database.newSession(username,
										sessionID.toString())) {
									returnData.put("session_id", sessionID);
									returnData.put("status", "OK");
								} else {
									returnData.put("status", FlowServer.ERROR);
								}
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
			// Allow the user to log in from another client
			case "end_session":
				returnData.put("status", this.database.removeSession(
						data.get("session_id", UUID.class).toString()));
				break;
			// Modifications to a user account
			case "user":
				String userCmdType = data.get("user_type", String.class);
				switch (userCmdType) {
				// Creating a new account
				case "REGISTER": {
					String username = data.get("username", String.class),
							password = data.get("password", String.class);

					// Verify that the username and password meet the specified
					// standards
					if (!Validator.validIdentification(username))
						returnData.put("status", "USERNAME_INVALID");
					else if (!Validator.validIdentification(username))
						returnData.put("status", "PASSWORD_INVALID");
					else {
						returnData.put("status",
								this.database.addUser(username, password));
						DataManagement.getInstance()
								.addUser(new User(username, password));
					}
				}
					break;
				// Deleting an account and removing all associated data
				case "CLOSE_ACCOUNT":
					try {
						String username = this.database.getUsername(
								data.get("session_id", UUID.class).toString());

						// Remove user from file system
						if (!DataManagement.getInstance()
								.removeUser(username)) {
							returnData.put("status", FlowServer.ERROR);
						} else {
							// Remove user from database
							returnData.put("status",
									this.database.closeAccount(username));
						}
					} catch (DatabaseException e) {
						e.printStackTrace();
						returnData.put("status", e.getMessage());
					}
					break;
				// Associates a new password with the logged in user account
				case "CHANGE_PASSWORD":
					try {
						// Get the username from the session id
						String username = this.database.getUsername(
								data.get("session_id", UUID.class).toString()),
								password = data.get("new_password",
										String.class);
						returnData.put("status", this.database
								.changePassword(username, password));
					} catch (DatabaseException e) {
						e.printStackTrace();
						returnData.put("status", e.getMessage());
					}
					break;
				}
				break;
			// Retrieve the UUIDs of all projects which a user has at least view
			// access to
			case "list_projects":
				try {
					ResultSet temp = this.database.getSessionInfo(
							data.get("session_id", UUID.class).toString());
					String username = temp.getString("Username");
					if (DataManagement.getInstance()
							.getUserByUsername(username) == null)
						returnData.put("status", "INVALID_SESSION_ID");
					else {
						ResultSet projects = this.database
								.getProjects(username);
						String[] response = Results.toStringArray("ProjectID",
								projects);
						if (response == null || response.length == 0 || response[0] == null) {
							returnData.put("projects", new UUID[0]);
						} else {
							returnData.put("projects", DataManipulation
									.getUUIDsFromArray(response));
						}
						returnData.put("status", "OK");
					}
				} catch (DatabaseException e) {
					e.printStackTrace();
					returnData.put("status", e.getMessage());
				} catch (SQLException e) {
					e.printStackTrace();
					returnData.put("status", FlowServer.ERROR);
				}
				break;
			// Creates a new project with the logged in user as the owner
			case "new_project":
				try {
					String projectName = data.get("project_name", String.class);
					String username = this.database.getUsername(
							data.get("session_id", UUID.class).toString());
					// Generate a random UUID
					UUID uuid = UUID.randomUUID();

					// Set the status equal to the result of creating the new
					// project
					String status = this.database.newProject(uuid.toString(),
							projectName, username);
					returnData.put("status", status);

					// Only provide the user with the project UUID if and only
					// if the project was created successfully
					if (status != null && status.equals("OK")) {
						returnData.put("project_uuid", uuid);
					}
				} catch (DatabaseException e) {
					e.printStackTrace();
					returnData.put("status", e.getMessage());
				}
				break;
			// Creates a new text file inside the specified directory and
			// project
			case "new_text_file":
				try {
					UUID projectUUID = data.get("project_uuid", UUID.class),
							sessionID = data.get("session_id", UUID.class);

					// Verify if the user has at least edit access to the
					// project
					if (this.database.verifyPermissions(sessionID.toString(),
							projectUUID.toString(), SQLDatabase.EDIT)) {
						UUID directoryUUID = data.get("directory_uuid",
								UUID.class), fileUUID = UUID.randomUUID(),
								versionUUID = UUID.randomUUID();
						String documentName = data.get("file_name",
								String.class);

						// Validate that the file name meets the specified
						// standards
						if (Validator.validFileName(documentName)) {
							// Only create the directories and file in the file
							// system, and provide the user with the file UUID
							// if all the database operations were completed
							// successfully
							String status = this.database.newFile(
									fileUUID.toString(), documentName,
									projectUUID.toString(), directoryUUID.toString());
							if (status.equals("OK")) {
								status = this.database.newVersion(
										fileUUID.toString(),
										versionUUID.toString());
								if (status.equals("OK")) {
									// Create the document
									VersionText newTextDocument = new VersionText();
									VersionManager.getInstance().addTextVersion(
											fileUUID, versionUUID,
											newTextDocument);
									// Save the document to the disk
									DataManagement.getInstance()
											.flushTextToDisk(fileUUID,
													versionUUID,
													newTextDocument);
									returnData.put("file_uuid", fileUUID);
								}
							}
							returnData.put("status", status);
						} else {
							returnData.put("status", "INVALID_FILE_NAME");
						}
					} else {
						returnData.put("status", "ACCESS_DENIED");
					}
				} catch (DatabaseException e) {
					e.printStackTrace();
					returnData.put("status", FlowServer.ERROR);
				}
				break;
			// Creates a new directory inside the specified directory and
			// project
			case "new_directory":
				try {
					UUID projectUUID = data.get("project_uuid", UUID.class);
					UUID parentDirectoryUUID = data.get("parent_directory_uuid",
							UUID.class);
					UUID sessionID = data.get("session_id", UUID.class);

					// Verify if the user has at least edit access to the
					// project
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
					e.printStackTrace();
					returnData.put("status", FlowServer.ERROR);
				}
				break;
			// Modifies data associated with the specified project
			case "project_modify": {
				UUID projectUUID = data.get("project_uuid", UUID.class),
						sessionID = data.get("session_id", UUID.class);
				try {
					switch (data.get("project_modify_type", String.class)) {
					// Modify the access levels of users to the project
					case "MODIFY_COLLABORATOR":
						String username = data.get("username", String.class);
						int accessLevel = (int) data.get("access_level",
								Byte.class);

						// Verify if the user is the owner of the project and
						// provide a more open access updating method
						if (this.database.verifyPermissions(
								sessionID.toString(), projectUUID.toString(),
								SQLDatabase.OWNER)) {
							returnData.put("status",
									this.database.ownerUpdateAccess(accessLevel,
											projectUUID.toString(), username));

							// Otherwise, verify if the user has at least edit
							// access to the project, and provide a more
							// restricted update access method
						} else if (this.database.verifyPermissions(
								sessionID.toString(), projectUUID.toString(),
								SQLDatabase.EDIT)) {
							returnData.put("status",
									this.database.restrictedUpdateAccess(
											accessLevel, projectUUID.toString(),
											username));
						} else {
							returnData.put("status", "ACCESS_DENIED");
						}
						break;
					// Change the name associated with the project
					case "RENAME_PROJECT": {
						String newName = data.get("new_name", String.class);
						returnData.put("status", this.database.renameProject(
								projectUUID.toString(), newName));
					}
						break;
					// Completely delete the project and all associated files
					case "DELETE_PROJECT":
						// Verify if the user is the owner of the project
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
			// Modify data associated with the specified directory
			case "directory_modify": {
				try {
					UUID directoryUUID = data.get("directory_uuid", UUID.class);
					String sessionID = data.get("session_id", UUID.class)
							.toString(),
							projectUUID = this.database
									.getProjectUUIDFromDirectory(
											directoryUUID.toString());

					// Verify if the user has at least edit access to the
					// project
					if (this.database.verifyPermissions(sessionID, projectUUID,
							SQLDatabase.EDIT)) {
						String type = data.get("mod_type", String.class);
						switch (type) {
						// Associate a new name with the directory
						case "RENAME":
							String newName = data.get("new_name", String.class);
							returnData.put("status",
									this.database.renameDirectory(
											directoryUUID.toString(), newName));
							break;
						// Completely delete the directory and all associated
						// files
						case "DELETE":
							returnData.put("status", this.database
									.deleteDirectory(directoryUUID.toString()));
							break;
						}
					} else {
						returnData.put("status", "ACCESS_DENIED");
					}
				} catch (DatabaseException e) {
					e.printStackTrace();
					returnData.put("status", e.getMessage());
				}
			}
				break;
			// Modify meta data associated with the specified file
			case "file_metadata_modify":
				try {
					UUID fileUUID = data.get("file_uuid", UUID.class);
					String sessionID = data.get("session_id", UUID.class)
							.toString(),
							projectUUID = this.database.getProjectUUIDFromFile(
									fileUUID.toString());

					// Verify if the user has at least edit access to the
					// project
					if (this.database.verifyPermissions(sessionID, projectUUID,
							SQLDatabase.EDIT)) {
						String modType = data.get("mod_type", String.class);
						switch (modType) {
						// Associate a new name with the directory
						case "RENAME":
							String name = data.get("name", String.class);
							if (Validator.validFileName(name)) {
								returnData.put("status", this.database
										.renameFile(fileUUID.toString(), name));
							} else {
								returnData.put("status", "FILE_NAME_INVALID");
							}
							break;
						// Completely delete the file and all associated
						// versions
						case "DELETE":
							DataManagement.getInstance()
									.removeFileByUUID(fileUUID);
							returnData.put("status", this.database
									.deleteFile(fileUUID.toString()));
							break;
						}
					} else {
						returnData.put("status", "ACCESS_DENIED");
					}
				} catch (DatabaseException e) {
					LOGGER.severe(e.getMessage());
					returnData.put("status", FlowServer.ERROR);
				}
				break;
			// Retrieves information about a specific project
			case "project_info":
				try {
					String projectUUID = data.get("project_uuid", UUID.class)
							.toString();
					String sessionID = data.get("session_id", UUID.class)
							.toString();

					// Verify if the user has at least view access to the
					// project
					if (this.database.verifyPermissions(sessionID,
							projectUUID)) {
						ResultSet databaseResponse = this.database
								.getProjectInfo(projectUUID);
						returnData.put("project_name",
								databaseResponse.getString("ProjectName"));
						returnData.put("editors", this.database
								.getUsers(projectUUID, SQLDatabase.EDIT));
						returnData.put("viewers", this.database
								.getUsers(projectUUID, SQLDatabase.VIEW));

						// Get the first index of the array as their could only
						// be one owner of the project
						returnData.put("owner", this.database
								.getUsers(projectUUID, SQLDatabase.OWNER)[0]);

						// If no exceptions were thrown up to this point, no
						// errors occurred in the data retrieval
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
			// Retrieves information about a specific directory
			case "directory_info":
				try {
					// Load the required data from the data packet
					UUID directoryUUID = data.get("directory_uuid", UUID.class);

					String sessionID = data.get("session_id", UUID.class)
							.toString(),
							projectUUID = this.database
									.getProjectUUIDFromDirectory(
											directoryUUID.toString());

					// Verify if the user has at least view access to the
					// project
					if (this.database.verifyPermissions(sessionID,
							projectUUID)) {
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
								DataManipulation.getUUIDsFromArray(Results
										.toStringArray("DocumentID", results)));

						// Add information from all sub directories located
						// inside the specified directory
						results = this.database.getDirectoriesInDirectory(
								directoryUUID.toString());
						returnData.put("child_directories",
								DataManipulation.getUUIDsFromArray(
										Results.toStringArray("DirectoryID",
												results)));

						// If no exceptions were thrown up to this point, no
						// errors occurred in the data retrieval.
						returnData.put("status", "OK");
					} else {
						returnData.put("status", "ACCESS_DENIED");
					}
				} catch (DatabaseException e) {
					e.printStackTrace();
					LOGGER.severe(e.getMessage());
					data.put("status", FlowServer.ERROR);
				} catch (SQLException e) {
					e.printStackTrace();
					LOGGER.severe(e.getMessage());
					data.put("status", e.getMessage());
				}
				break;
			// Retrieves information about a specific file
			case "file_info":
				try {
					// Load the required data from the data packet
					String fileUUID = data.get("file_uuid", UUID.class)
							.toString(),
							sessionID = data.get("session_id", UUID.class)
									.toString(),
							projectUUID = this.database
									.getProjectUUIDFromFile(fileUUID);

					// Verify if the user has at least edit access to the
					// project
					if (this.database.verifyPermissions(sessionID,
							projectUUID)) {
						// And file meta data information
						ResultSet results = this.database.getFileInfo(fileUUID);
						returnData.put("file_name",
								results.getString("DocumentName"));
						returnData.put("file_type",
								results.getString("FileType"));

						// Add the UUIDs of all of the version of the file
						returnData.put("file_versions",
								DataManipulation.getUUIDsFromArray(this.database
										.getFileVersions(fileUUID)));
						returnData.put("status", "OK");
					} else {
						returnData.put("status", "ACCESS_DENIED");
					}
				} catch (DatabaseException e) {
					e.printStackTrace();
					LOGGER.severe(e.getMessage());
					returnData.put("status", e.getMessage());
				} catch (SQLException e) {
					e.printStackTrace();
					LOGGER.severe(e.getMessage());
					returnData.put("status", FlowServer.ERROR);
				}
				break;
			// Retrieves information about a specific version
			case "version_info":
				try {
					String versionUUID = data.get("version_uuid", UUID.class)
							.toString(),
							sessionID = data.get("session_id", UUID.class)
									.toString(),
							projectUUID = this.database
									.getProjectUUIDFromVersion(versionUUID);

					// Verify if the user has at least edit access to the
					// project
					if (this.database.verifyPermissions(sessionID,
							projectUUID)) {

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
				break;
			// Retrieves the specified version of a file
			case "request_version":
				try {
					UUID fileUUID = data.get("file_uuid", UUID.class),
							versionUUID = data.get("version_uuid", UUID.class);
					String sessionID = data.get("session_id", UUID.class)
							.toString(),
							projectUUID = this.database.getProjectUUIDFromFile(
									fileUUID.toString());

					// Verify if the user has at least edit access to the
					// project
					if (this.database.verifyPermissions(sessionID,
							projectUUID)) {
						byte[] bytes;

						// Retrieve the data of the file differently based on
						// the file type (as arbitrary document versions are
						// stored as full copies)
						String fileType = this.database
								.getFileType(fileUUID.toString());
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
			// Retrieves the latest version of a file
			case "file_request":
				try {
					UUID fileUUID = data.get("file_uuid", UUID.class);
					String sessionID = data.get("session_id", UUID.class)
							.toString(),
							projectUUID = this.database.getProjectUUIDFromFile(
									fileUUID.toString());

					// Verify if the user has at least edit access to the
					// project
					if (this.database.verifyPermissions(sessionID,
							projectUUID)) {
						UUID versionUUID = UUID.fromString(this.database
								.getLatestVersionUUID(fileUUID.toString()));
						returnData.put("version_uuid", versionUUID);
						byte[] bytes;

						// Retrieve the data of the file differently based on
						// the file type (as arbitrary document versions are
						// stored as full copies)
						String fileType = this.database
								.getFileType(fileUUID.toString());
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
					}
				} catch (DatabaseException e) {
					e.printStackTrace();
					returnData.put("status", e.getMessage());
				}
				break;
			// Modify the contents of a text document
			case "file_text_modify":
				try {
					UUID fileUUID = data.get("file_uuid", UUID.class);
					int index = data.get("idx", Integer.class);
					String username = this.database.getUsername(
							data.get("session_id", UUID.class).toString());
					UUID latestVersionUUID = UUID.fromString(this.database
							.getLatestVersionUUID(fileUUID.toString()));
					VersionText td = VersionManager.getInstance()
							.getTextByVersionUUID(latestVersionUUID);
					switch (data.get("mod_type", String.class)) {
					// Insert characters into the file
					case "INSERT": {
						String str = data.get("str", String.class);

						// Inform all other clients who are editing the file
						// of the changes
						DocumentCallbackEvent event = new DocumentCallbackEvent(
								DocumentCallbackEvent.DocumentCallbackType.INSERT,
								fileUUID, username, index, str, -1);
						PersistentHandleManager.getInstance()
								.doCallbackEvent(fileUUID, event);
						td.insert(str, index);

					}
						break;
					// Remove characters from the file
					case "DELETE": {
						int length = data.get("len", Integer.class);

						// Inform all other clients who are editing the file
						// of the changes
						DocumentCallbackEvent event = new DocumentCallbackEvent(
								DocumentCallbackEvent.DocumentCallbackType.DELETE,
								fileUUID, username, index, null, length);
						PersistentHandleManager.getInstance()
								.doCallbackEvent(fileUUID, event);
						if (length == -1) {
							td.setDocumentText("");
							LOGGER.info(
									"clearing document because of negative length");
						} else {
							td.delete(index, length);
						}
					}
						break;
					// Move the user's cursor position
					case "MOVE": {
						DocumentCallbackEvent event = new DocumentCallbackEvent(
								DocumentCallbackEvent.DocumentCallbackType.MOVE,
								fileUUID, username, index, null, -1);
						PersistentHandleManager.getInstance()
								.doCallbackEvent(fileUUID, event);
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
				returnData.put("status", "INVALID_REQUEST_TYPE");
				break;
			}
			this.psocket.send(returnData);
			LOGGER.info("response: " + returnData.toString());
		} catch (IOException e) {
			LOGGER.warning("communication error: " + e.getMessage());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			LOGGER.warning("ClassNotFoundException error: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			// Catch all possible exceptions to prevent mallacious requests from
			// crashing the server.
			LOGGER.severe("Internal Server Error: " + e.getMessage());
			e.printStackTrace();
			Data data = new Data();
			data.put("status", FlowServer.ERROR);
			try {
				this.psocket.send(data);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		try {
			this.socket.close();
		} catch (IOException e) {
			LOGGER.severe("failure to close socket");
		}
	}
}
