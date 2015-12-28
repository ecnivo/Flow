package network;

import message.Data;

import java.io.*;
import java.net.Socket;

/**
 * Communication for the Client.
 *
 * Created by Netdex on 12/27/2015.
 */
public class FMLNetworker {

    private String ip;
    private int port;

    public FMLNetworker(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    public Data send(Data data) throws IOException, ClassNotFoundException {
        Socket socket = new Socket(ip, port);
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        synchronized (data) {
            oos.writeObject(data);
            oos.flush();
        }
        return (Data) ois.readObject();
    }
}
