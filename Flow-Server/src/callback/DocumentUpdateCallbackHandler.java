package callback;

import database.SQLDatabase;
import message.Data;
import network.DataSocket;
import server.DataManagement;
import server.VersionManager;
import util.DatabaseException;

import java.io.IOException;
import java.util.UUID;

/**
 * A callback handler specifically handling document modification Created by
 * Gordon Guan on 1/15/2016.
 */
public class DocumentUpdateCallbackHandler extends CallbackHandler {
	private final UUID documentUUID;

	public DocumentUpdateCallbackHandler(PersistentClientHandle handle,
			UUID documentUUID) {
		super(handle);
		this.documentUUID = documentUUID;
	}

	/**
	 * @return the document UUID this callback handler is associated with
	 */
	public UUID getDocumentUUID() {
		return documentUUID;
	}

	@Override
	public void onCallbackEvent(CallbackEvent arg0) throws IOException {
		if (!(arg0 instanceof DocumentCallbackEvent))
			throw new IllegalArgumentException(
					"event must be document callback event!");
		DocumentCallbackEvent event = (DocumentCallbackEvent) arg0;
		DataSocket dataSocket = this.getHandle().getDataSocket();
		// Send the event to the client
		Data data = new Data("async_callback");
		data.put("event", event);
		dataSocket.send(data);
	}

	@Override
	public void onRegister(RegisterEvent event) {

	}

	@Override
	public void onUnregister(RegisterEvent event) {
		try {
			// Save the file to disk when the listener is unregistered
			UUID latestVersionUUID = UUID.fromString(SQLDatabase.getInstance()
					.getLatestVersionUUID(event.UUID.toString()));
			DataManagement.getInstance().flushTextToDisk(event.UUID,
					latestVersionUUID, VersionManager.getInstance()
							.getTextByVersionUUID(latestVersionUUID));
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}
}
