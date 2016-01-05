package network;

import message.Data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Communication for the Client.
 * <p>
 * Created by Netdex on 12/27/2015.
 */
public class FMLNetworker {

    private String ip;
    private int port;

    private static Logger L = Logger.getLogger("FMLNetworker");

    public FMLNetworker(String ip, int port) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%n");
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
        L.info("initiating send protocol");
        try {
            Socket socket = new Socket(ip, port);

            L.info("writing message: " + data.toString());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(data);
            oos.flush();

            L.info("reading response...");
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Data re = (Data) ois.readObject();
            L.info("response: " + re.toString());

            socket.close();
            ois.close();
            oos.close();
            return re;
        } catch (Exception e) {
            L.severe("error during send operation");
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
