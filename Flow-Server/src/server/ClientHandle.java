package server;

import message.Data;
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
                final Data authRequest = psocket.receivePackage(Data.class);
                if (!authRequest.getType().equals("auth"))
                    return;
                String user = authRequest.get("username", String.class);
                String pass = authRequest.get("password", String.class);

                authenticated = true; // call bimde's authentication code somewhere
                Data authStatusData = new Data("auth");
                if (authenticated) {
                    this.uuid = UUID.randomUUID();
                    authStatusData.put("status", "OK");
                    authStatusData.put("session_id", uuid);
                } else {
                    authStatusData.put("status", "INVALID");
                }
                psocket.sendPackage(authStatusData);
            }
            while (socket.isConnected()) {
                Data data = psocket.receivePackage(Data.class);
                String type = data.get("type", String.class);
                switch (type) {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
