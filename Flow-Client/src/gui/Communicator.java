package gui;

import java.io.IOException;

import message.Data;
import network.FMLNetworker;

public class Communicator {

    private static final String HOST = "127.0.0.1";
    private final static int PORT = 1234;

    private static String currLoggedIn;

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

    public static void setUser(String newUser) {
	currLoggedIn = newUser;
    }

    public static String getCurrLoggedIn() {
	return currLoggedIn;
    }
}
