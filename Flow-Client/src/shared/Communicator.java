
package shared;

import java.io.IOException;
import java.util.UUID;

import javax.swing.JOptionPane;

import message.Data;
import network.FMLNetworker;
import callback.TextModificationListener;

/**
 * Central communications handler
 * 
 * @author Vince Ou
 *
 */
public class Communicator {

	private static UUID			sessionID;
	private static String		username;
	private static FMLNetworker	networker;

	/**
	 * Creates a new networker using FML
	 */
	public static void initComms(String host, int port) {
		networker = new FMLNetworker(host, port, 10225);// TODO hardcoded
	}

	/**
	 * Sends a Data packet and the data that comes back from the server
	 * 
	 * @param data
	 *        the data to be sent
	 * @return the response
	 */
	public static Data communicate(Data data) {
		// Starts a timer so that the client doesn't freeze because the server froze
		Thread timer = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		timer.start();
		try {
			// Try sending the data
			return networker.send(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			// But it must finish communicating within 30 seconds or else the communicator will stop
			// because of time out
			timer.join(30000);
		} catch (InterruptedException e) {
			JOptionPane.showConfirmDialog(null, "Connection to server timed out.", "Connection to server failed", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Gets the session ID
	 * 
	 * @return the SessionID in use
	 */
	public static UUID getSessionID() {
		return sessionID;
	}

	/**
	 * Sets the session ID
	 * 
	 * @param sessionID
	 *        the new session ID
	 */
	public static void setSessionID(UUID sessionID) {
		Communicator.sessionID = sessionID;
	}

	/**
	 * Initialize the File Synchronizer
	 */
	public static void initAsync() {
		try {
			networker.initAsync();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds a file change listener for file changes
	 * 
	 * @param listener
	 *        the new change listener to register
	 * @param fileUUID
	 *        the file to add the listener to
	 */
	public static void addFileChangeListener(TextModificationListener listener, UUID fileUUID) {
		networker.registerCallbackListener(listener, fileUUID);
	}

	/**
	 * @param fileUUID
	 *        the file to remove the listener from
	 */
	public static void removeFileChangeListener(UUID fileUUID) {
		networker.unregisterCallbackListener(fileUUID);
	}

	/**
	 * Gets the username
	 * 
	 * @return the username in use
	 */
	public static String getUsername() {
		return username;
	}

	/**
	 * Sets the username
	 * 
	 * @param username
	 *        the username in use
	 */
	public static void setUsername(String username) {
		Communicator.username = username;
	}
}
