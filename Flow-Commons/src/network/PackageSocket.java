package network;

import java.io.*;
import java.net.Socket;

/**
 * A wrapper around a socket which has the ability to send Parcelables over the network
 * <p>
 * Created by Netdex on 12/17/2015.
 */
public class PackageSocket {

    private Socket socket;

    private OutputStream os;
    private InputStream is;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public PackageSocket(Socket socket) throws IOException {
        this.socket = socket;
        this.os = socket.getOutputStream();
        this.is = socket.getInputStream();
        this.ois = new ObjectInputStream(is);
        this.oos = new ObjectOutputStream(oos);
    }

    public void sendPackage(Serializable serializable) throws IOException {
        synchronized (serializable) {
            oos.writeObject(serializable);
            oos.flush();
        }
    }

    public Object receivePackage() throws IOException, ClassNotFoundException {
        return ois.readObject();
    }

}
