package server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

import database.SQLDatabase;
import struct.FlowDirectory;
import struct.FlowDocument;
import struct.FlowFile;
import struct.FlowProject;
import struct.TextDocument;
import struct.User;
import util.DatabaseException;

public class FlowServer implements Runnable {

	private static Logger L = Logger.getLogger("FlowServer");
	public static String ERROR = "INTERNAL_SERVER_ERROR";
	private static final int PORT = 10244;
	// private static final int ARC_PORT = 10225;

	private SQLDatabase database;

	private static final int MAX_THREADS = 100;
	private Thread[] threadPool = new Thread[MAX_THREADS];

	private ArrayList<DocumentUpdateCallback> _DocumentUpdateCallbacks;

	public FlowServer() {
		this._DocumentUpdateCallbacks = new ArrayList<>();
		this.database = new SQLDatabase("data/FlowDatabse.db");
	}

	@Override
	public void run() {
		DataManagement.getInstance().init(new File("data"));
		try {
			L.info("started listening");
			ServerSocket serverSocket = new ServerSocket(PORT);

			while (serverSocket.isBound()) {
				Socket socket = serverSocket.accept();
				L.info("accepted connection from "
						+ socket.getRemoteSocketAddress());
				int i = 0;
				do {
					i %= MAX_THREADS;
					if (threadPool[i] != null && !threadPool[i].isAlive())
						threadPool[i--] = null;
					++i;
				} while (threadPool[i] != null);

				L.info("Request assigned worker thread " + i);
				Thread t = new Thread(new ClientRequestHandle(this, socket));
				t.start();
				threadPool[i] = t;
			}
			serverSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected UUID newSession(String username) {
		UUID sessionId = UUID.randomUUID();
		this.database.newSession(username, sessionId.toString());
		return sessionId;
	}

	/**
	 * Retrieves the FlowProject associated with the specified project UUID.
	 * 
	 * @param projectId
	 *            the UUID associated with the desired project.
	 * @return the FlowProject associated with the specified project UUID.
	 * @throws DatabaseException
	 *             if there is an error accessing the database.
	 */
	protected FlowProject getProject(String projectId)
			throws DatabaseException {
		ResultSet temp = this.database.getProjectInfo(projectId);
		try {
			if (!temp.next()) {
				throw new DatabaseException("PROJECT_NOT_FOUND");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(FlowServer.ERROR);
		}
		try {
			String owner = temp.getString("OwnerUsername");
			return DataManagement.getInstance()
					.getProjectFromUUID(UUID.fromString(projectId));
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(FlowServer.ERROR);
		}
	}

	public FlowDocument getFile(String fileId, String projectId)
			throws DatabaseException {
		ResultSet fileData = null;
		try {
			fileData = this.database.getFile(fileId);
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		ResultSet projectData = null;
		try {
			projectData = this.database.getProjectInfo(projectId);
		} catch (DatabaseException e) {
			e.printStackTrace();
		}

		try {
			fileData.next();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(FlowServer.ERROR);
		}
		try {
			projectData.next();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(FlowServer.ERROR);
		}

		String parentDirectoryId = null;
		try {
			parentDirectoryId = fileData.getString("ParentDirectoryID");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FlowFile file;
		try {
			// TODO DEFINITELY CHANGE THIS
			// TODO implement deserialization of current flow file
			file = new FlowFile(
					(parentDirectoryId != null
							? new FlowDirectory(parentDirectoryId)
							: new FlowProject(
									projectData.getString("ProjectName"),
									new User(projectData
											.getString("OwnerUsername")))),
					fileData.getString("DocumentName"),
					UUID.fromString(fileId));
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(FlowServer.ERROR);
		}

		// TODO Generate file data from saved file
		return new TextDocument(file, new Date());
	}

	protected SQLDatabase getDatabase() {
		return this.database;
	}

	public static void main(String[] args) throws IOException,
			KeyManagementException, NoSuchAlgorithmException {
		System.setProperty("java.util.logging.SimpleFormatter.format",
				"%4$s: %5$s%n");
		FlowServer server = new FlowServer();
		new Thread(server).start();
		// TEST CODE
	}
}
