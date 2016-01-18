package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 * A wrapper around a socket which has the ability to send Parcelables over the network
 * <p>
 * Created by Netdex on 12/17/2015.
 */
public class DataSocket {

    private Socket socket;

    private final ObjectInputStream ois;
    private final ObjectOutputStream oos;

    public DataSocket(Socket socket) throws IOException {
        this.socket = socket;
        this.oos = new ObjectOutputStream(socket.getOutputStream());
        oos.flush();
        this.ois = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * Sends a serializable over the network
     *
     * @param serializable The serializable to send
     * @throws IOException When something nasty happens
     */
    public synchronized void send(Serializable serializable) throws IOException {
        synchronized (oos) {
            synchronized (ois) {
                oos.writeObject(serializable);
                oos.flush();
            }
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
    public synchronized <T extends Serializable> T receive(Class<T> clazz) throws IOException, ClassNotFoundException {
        synchronized (ois) {
            synchronized (oos) {
                return clazz.cast(ois.readObject());
            }
        }
    }

    /**
     * Receives a serializable over the network, auto cast, type infer
     *
     * @param <T> The type of the class of the serializable
     * @return The serializable from the network
     * @throws IOException            When something nasty happens
     * @throws ClassNotFoundException When we don't have a copy of the class from remote
     */
    public synchronized <T extends Serializable> T receive() throws IOException, ClassNotFoundException {
        synchronized (ois) {
            synchronized (oos) {
                return (T) ois.readObject();
            }
        }
    }

    public void close() throws IOException {
        oos.close();
        ois.close();
    }

    public Socket getSocket() {
        return socket;
    }


}
