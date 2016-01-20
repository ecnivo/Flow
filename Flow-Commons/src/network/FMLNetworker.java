package network;

import callback.*;
import message.Data;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Communication for the Client.
 * <p>
 * Created by Gordon Guan on 12/27/2015.
 */
public class FMLNetworker {

    private static final Logger L = Logger.getLogger("FLOW");
    private final String ip;
    private final int port;
    private final int arcport;
    private DataSocket asyncSocket;
    private EventPusher pusher;

    public FMLNetworker(String ip, int port, int arcport) {
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
     */
    public Data send(Data data) throws IOException {
        try {
            Socket socket = new Socket(ip, port);
            socket.setPerformancePreferences(1, 0, 0);
            socket.setTcpNoDelay(true);
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

    /**
     * Initialize asynchronous listeners
     *
     * @param sessionUUID The sessionID of the user
     * @throws IOException
     */
    public void initAsync(UUID sessionUUID) throws IOException {
        Socket socket = new Socket(ip, arcport);
        this.asyncSocket = new DataSocket(socket);
        this.pusher = new EventPusher(asyncSocket, sessionUUID);
        new Thread(pusher).start();
    }

    /**
     * Kills asynchronous listeners
     * @throws IOException
     */
    public void killAsync() throws IOException {
        pusher.kill();
        asyncSocket.getSocket().close();
        asyncSocket.close();
    }

    /**
     * Register a client side listener
     * @param chngListener The listener
     * @param assocUUID UUID of the listener
     * @return success or not
     */
    public void registerCallbackListener(CallbackListener chngListener, UUID assocUUID) {
        if (asyncSocket == null) {
            L.severe("attempted to register callback when async is not initiated!");
            return;
        }
        try {
            // Send the server a message letting them know a listener was registered
            Data asyncCallbackRequest = new Data("async");
            if (chngListener instanceof TextModificationListener) {
                asyncCallbackRequest.put("ltype", CallbackEvent.CallbackEventType.DOCUMENT_CALLBACK);
            }
            asyncCallbackRequest.put("rtype", RegisterEvent.RegisterType.REGISTER);
            asyncCallbackRequest.put("uuid", assocUUID);
            asyncSocket.send(asyncCallbackRequest);
            pusher.registerListener(assocUUID, chngListener);
            L.info("registered callback!");
            return;
        } catch (IOException e) {
            L.severe("ioexception when registering callback!");
            return;
        }
    }

    /**
     * Unregister a client side listener
     * @param assocUUID The UUID of the listener
     * @return success or not
     */
    public void unregisterCallbackListener(UUID assocUUID) {
        if (asyncSocket == null) {
            L.severe("attempted to deregister callback when async is not initiated!");
            return;
        }
        try {
            // Send the server a message letting them know a listener was unregistered
            Data cancelAsync = new Data("async");
            cancelAsync.put("rtype", RegisterEvent.RegisterType.UNREGISTER);
            cancelAsync.put("uuid", assocUUID);
            asyncSocket.send(cancelAsync);
            pusher.unregisterListener(assocUUID);
            L.info("unregistered callback!");
            return;
        } catch (IOException e) {
            L.severe("io exception when deregistering callback!");
            return;
        }
    }
}
