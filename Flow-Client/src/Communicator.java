import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

import message.Data;
import network.DataSocket;

public class Communicator {

    private static final String HOST = "127.0.0.1";
    private final static int PORT = 1234;

    private static DataSocket packageSender;

    public static void initComms() {
	try {
	    Socket socket = new Socket(HOST, PORT);
	    packageSender = new DataSocket(socket);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public static void communicate(Data data) {
	try {
	    packageSender.send(data);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public static <T extends Serializable> T communicate(Data data, T returnType) {
	communicate(data);
	try {
	    return (T) packageSender.receive(returnType.getClass());
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return null;
    }
}
