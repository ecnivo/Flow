package network;

import message.Data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;

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
            System.out.println("I GOT HERE!#Q&*");
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            synchronized (data) {
                oos.writeObject(data);
                oos.flush();
            }
            Data re = (Data) ois.readObject();
            socket.close();
            ois.close();
            oos.close();
            System.out.println("Y O I GOT HERE TOO");
            return re;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void registerFlowDocumentChangeListener(FlowDocumentChangeListener chngListener, UUID flowDocumentUUID) throws IOException {
        Data asyncCallbackRequest = new Data("document_async");
        asyncCallbackRequest.put("rtype", "register");
        asyncCallbackRequest.put("doc_id", flowDocumentUUID);
        send(asyncCallbackRequest);
        chngListener.init(ip);
        chngListener.listen();
    }

    public void deregisterFlowDocumentChangeListener(FlowDocumentChangeListener chngListener) {
        Data cancelAsync = new Data("document_async");
        cancelAsync.put("rtype", "deregister");

        chngListener.stopListening();
    }
}
