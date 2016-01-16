package network;

import callback.CallbackListener;
import callback.EventPusher;
import message.Data;

import java.io.IOException;
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
    private int arcport;

    private DataSocket asyncSocket;
    private EventPusher pusher;

    private static Logger L = Logger.getLogger("FMLNetworker");

    public FMLNetworker(String ip, int port, int arcport) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%n");
        this.ip = ip;
        this.port = port;
        this.arcport = arcport;
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
            DataSocket ds = new DataSocket(socket);

            L.info("writing message: " + data.toString());
            ds.send(data);

            L.info("reading response...");
            Data re = ds.receive(Data.class);
            L.info("response: " + re.toString());

            socket.close();
            ds.close();
            return re;
        } catch (Exception e) {
            L.severe("error during send operation");
            e.printStackTrace();
            return null;
        }
    }

    public void initAsync() throws IOException {
        Socket socket = new Socket(ip, arcport);
        this.asyncSocket = new DataSocket(socket);
        this.pusher = new EventPusher(asyncSocket);
        new Thread(pusher).start();
    }

    public boolean registerCallbackListener(CallbackListener chngListener, UUID assocUUID) {
        try {
            Data asyncCallbackRequest = new Data("async");
            asyncCallbackRequest.put("rtype", "REGISTER");
            asyncCallbackRequest.put("uuid", assocUUID);
            asyncSocket.send(asyncCallbackRequest);
            pusher.registerListener(assocUUID, chngListener);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean unregisterCallbackListener(UUID assocUUID) {
        try {
            Data cancelAsync = new Data("async");
            cancelAsync.put("rtype", "UNREGISTER");
            asyncSocket.send(cancelAsync);
            pusher.unregisterListener(assocUUID);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
