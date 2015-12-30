package shared;

import java.io.IOException;
import java.util.UUID;

import message.Data;
import network.FMLNetworker;

public class Communicator {

    private static final String HOST = "127.0.0.1";
    private final static int PORT = 1234;

    private static UUID sessionID;

    private static FMLNetworker packageSender;

    public static void initComms() {
	packageSender = new FMLNetworker(HOST, PORT);
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

    
}
