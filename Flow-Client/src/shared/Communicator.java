package shared;

import java.io.IOException;
import java.util.UUID;

import message.Data;
import network.FMLNetworker;
import network.FileChangeListener;

public class Communicator {

    private static UUID sessionID;

    private static FMLNetworker packageSender;

    public static void initComms(String host, int port) {
	packageSender = new FMLNetworker(host, port);
    }

    @SuppressWarnings("unchecked")
    public static Data communicate(Data data) {
	try {
	    return packageSender.send(data);
	} catch (IOException e) {
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

    public static void addFileChangeListener(FileChangeListener listener, UUID fileUUID) {
	packageSender.registerFileChangeListener(listener, fileUUID);
    }

    public static void removeFileChangeListener(FileChangeListener listener) {
	packageSender.unregisterFileChangeListener(listener);
    }
}
