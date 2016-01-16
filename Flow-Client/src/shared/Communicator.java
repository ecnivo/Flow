package shared;

import callback.TextModificationListener;
import message.Data;
import network.FMLNetworker;

import java.io.IOException;
import java.util.UUID;

public class Communicator {

    private static UUID sessionID;

    private static FMLNetworker packageSender;

    public static void initComms(String host, int port) {
        packageSender = new FMLNetworker(host, port, 10225);// TODO hardcoded
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

    public static void addFileChangeListener(TextModificationListener listener, UUID fileUUID) {
        packageSender.registerCallbackListener(listener, fileUUID);
    }

    public static void removeFileChangeListener(UUID fileUUID) {
        packageSender.unregisterCallbackListener(fileUUID);
    }
}
