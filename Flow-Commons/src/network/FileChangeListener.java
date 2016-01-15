package network;

import message.Data;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Netdex on 12/29/2015.
 */
public abstract class FileChangeListener {

    private static final int ARC_PORT = 10225;
    private boolean cont = true;

    private Socket socket;
    private DataSocket dsocket;

    public FileChangeListener() {

    }

    protected void init(String ip) throws IOException {
        this.socket = new Socket(ip, ARC_PORT);
        this.dsocket = new DataSocket(socket);
    }

    protected void listen() {
        new Thread() {
            public void run() {
                try {
                    while (socket.isConnected() && cont) {
                        Data msg = dsocket.receive(Data.class);
                        DocumentModificationEvent dme = new DocumentModificationEvent(
                                msg.get("mod_type", String.class),
                                msg.get("line", Integer.class),
                                msg.get("idx", Integer.class),
                                msg.get("str", String.class),
                                msg.get("len", Integer.class));
                        if (cont)
                            onFileUpdate(dme);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    protected void stopListening() {
        cont = false;
    }

    public abstract void onFileUpdate(DocumentModificationEvent event);
}
