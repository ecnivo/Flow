package shared;

import java.io.IOException;
import java.util.UUID;

import message.Data;
import network.FMLNetworker;
import callback.TextModificationListener;

public class Communicator {

    private static UUID sessionID;

    private static FMLNetworker networker;

    public static void initComms(String host, int port) {
        networker = new FMLNetworker(host, port, 10225);// TODO hardcoded
    }

    @SuppressWarnings("unchecked")
    public static Data communicate(Data data) {
	try {
	    return networker.send(data);
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
    
    public static void initAsync(){
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
}
