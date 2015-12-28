package network;

import message.Data;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;

/**
 * Communication for the Client.
 * <p>
 * Created by Netdex on 12/27/2015.
 */
public class FMLNetworker {

    private String ip;
    private int port;

    public FMLNetworker(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    /**
     * Sends a message to the server, and receives the subsequent response.
     *
     * @param data The data to send to the server
     * @return The response from the server
     * @throws IOException            When something nasty happens
     * @throws ClassNotFoundException When a class is missing client-side
     */
    public Data send(Data data) throws IOException {
        try {
            Socket socket = new Socket(ip, port);
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            synchronized (data) {
                oos.writeObject(data);
                oos.flush();
            }
            return (Data) ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }
}
