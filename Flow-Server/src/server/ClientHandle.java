package server;

import message.Message;
import network.PackageSocket;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class ClientHandle implements Runnable {

    private Socket socket;
    private PackageSocket psocket;

    private UUID uuid;

    public ClientHandle(Socket socket) throws IOException {
        this.socket = socket;
        this.psocket = new PackageSocket(socket);
    }

    @Override
    public void run() {
        try {
            while (socket.isConnected()) {
                final Message message = psocket.receivePackage(Message.class);
                String type = message.get("type", String.class);
                switch (type) {
                    case "auth":
                        String user = message.get("username", String.class);
                        String pass = message.get("password", String.class);
                        boolean authenticated = true; // call bimde's authentication code somewhere
                        Message authStatusMessage = new Message();
                        authStatusMessage.put("type", "auth");
                        if (authenticated) {
                            this.uuid = UUID.randomUUID();
                            authStatusMessage.put("status", "OK");
                            authStatusMessage.put("session_id", uuid);
                        } else {
                            authStatusMessage.put("status", "INVALID");
                        }
                        psocket.sendPackage(authStatusMessage);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
