package server;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

import message.Data;

public class ClientHandle implements Runnable {

	private Socket socket;
	private PackageSocket psocket;
	private Server server;

	private UUID uuid;

	public ClientHandle(Server server, Socket socket) throws IOException {
		this.socket = socket;
		this.psocket = new PackageSocket(socket);
		this.server = server;
	}

	@Override
	public void run() {
		try {
			this.socket.setSoTimeout(1000);
			boolean authenticated = false;
			while (!authenticated && socket.isConnected()) {
				final Data authRequest = psocket.receivePackage(Data.class);
				if (!authRequest.getType().equals("auth"))
					return;
				String user = authRequest.get("username", String.class);
				String pass = authRequest.get("password", String.class);

				// TODO Verify is this works properly
				authenticated = this.server.getDatabase().authenticate(user,
						pass);

				Data authStatusData = new Data("auth");
				if (authenticated) {
					this.uuid = this.server.newSession();
					authStatusData.put("status", "OK");
					authStatusData.put("session_id", this.uuid);

				} else {
					authStatusData.put("status", "INVALID");
				}
				this.psocket.sendPackage(authStatusData);
			}
			while (this.socket.isConnected()) {
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
