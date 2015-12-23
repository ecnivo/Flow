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

    /**
     * Sends a serializable over the network
     *
     * @param serializable The serializable to send
     * @throws IOException When something nasty happens
     */
    public void sendPackage(Serializable serializable) throws IOException {
        synchronized (serializable) {
            oos.writeObject(serializable);
            oos.flush();
        }
    }

    /**
     * Receives a serializable over the network
     *
     * @param clazz The class of the serializable
     * @param <T>   The type of the class of the serializable
     * @return The serializable from the network
     * @throws IOException            When something nasty happens
     * @throws ClassNotFoundException When we don't have a copy of the class from remote
     */
    public <T extends Serializable> T receivePackage(Class<T> clazz) throws IOException, ClassNotFoundException {
        return clazz.cast(ois.readObject());
    }

}
