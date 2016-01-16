package shared;

import java.io.IOException;
import java.util.UUID;

import javax.swing.JOptionPane;

import message.Data;
import network.FMLNetworker;
import callback.TextModificationListener;

public class Communicator {

    private static UUID sessionID;
    private static String username;
    private static FMLNetworker networker;

    public static void initComms(String host, int port) {
	networker = new FMLNetworker(host, port, 10225);// TODO hardcoded
    }

    @SuppressWarnings("unchecked")
    public static Data communicate(Data data) {
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
	    return networker.send(data);
	} catch (IOException e) {
	    e.printStackTrace();
	}
	try {
	    timer.join(30000);
	} catch (InterruptedException e) {
	    JOptionPane.showConfirmDialog(null, "Connection to server timed out.", "Connection to server failed", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
	    e.printStackTrace();
	}
	return null;
    }

    public static UUID getSessionID() {
	return sessionID;
    }

    public static void setSessionID(UUID sessionID) {
	Communicator.sessionID = sessionID;
    }

    public static void initAsync() {
	try {
	    networker.initAsync();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public static void addFileChangeListener(TextModificationListener listener, UUID fileUUID) {
	networker.registerCallbackListener(listener, fileUUID);
    }

    public static void removeFileChangeListener(UUID fileUUID) {
	networker.unregisterCallbackListener(fileUUID);
    }

    public static String getUsername() {
	return username;
    }

    public static void setUsername(String username) {
	Communicator.username = username;
    }
}
