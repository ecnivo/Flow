package callback;

import java.util.UUID;

/**
 * Represents a specific callback event in which a document is modified, and clients need to know
 * <p>
 * Created by Netdex on 1/15/2016.
 */
public class DocumentCallbackEvent extends CallbackEvent {
    public final DocumentCallbackType TYPE;
    public final String USERNAME;
    public final int INDEX;
    public final String ADDITION;
    public final int REMOVAL_LENGTH;
    public DocumentCallbackEvent(DocumentCallbackType TYPE, UUID documentUUID, String username, int INDEX, String ADDITION, int REMOVAL_LENGTH) {
        super(CallbackEventType.DOCUMENT_CALLBACK, documentUUID);
        this.TYPE = TYPE;
        this.USERNAME = username;
        this.INDEX = INDEX;
        this.ADDITION = ADDITION;
        this.REMOVAL_LENGTH = REMOVAL_LENGTH;
    }

    public enum DocumentCallbackType {
        INSERT,
        DELETE
    }

    @Override
    public String toString() {
        return String.format("type=%s;username=%s;index=%d;addition=%s;removal_length=%d",
                TYPE, USERNAME, INDEX, ADDITION, REMOVAL_LENGTH);
    }
}
