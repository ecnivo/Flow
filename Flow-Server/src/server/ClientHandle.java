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
            socket.setSoTimeout(1000);
            boolean authenticated = false;
            while(!authenticated && socket.isConnected())
            {
                final Message authRequest = psocket.receivePackage(Message.class);
                if (!authRequest.getType().equals("auth"))
                    return;
                String user = authRequest.get("username", String.class);
                String pass = authRequest.get("password", String.class);

                authenticated = true; // call bimde's authentication code somewhere
                Message authStatusMessage = new Message("auth");
                if (authenticated) {
                    this.uuid = UUID.randomUUID();
                    authStatusMessage.put("status", "OK");
                    authStatusMessage.put("session_id", uuid);
                } else {
                    authStatusMessage.put("status", "INVALID");
                }
                psocket.sendPackage(authStatusMessage);
            }
            while (socket.isConnected()) {
                Message message = psocket.receivePackage(Message.class);
                String type = message.get("type", String.class);
                switch (type) {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
