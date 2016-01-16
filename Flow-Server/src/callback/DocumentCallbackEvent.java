package callback;

/**
 * Created by Netdex on 1/15/2016.
 */
public class DocumentCallbackEvent extends CallbackEvent {
    public enum DocumentCallbackType {
        INSERT,
        DELETE
    }

    public DocumentCallbackType TYPE;
    public int LINE;
    public int INDEX;
    public String ADDITION;
    public int REMOVAL_LENGTH;

    public DocumentCallbackEvent(DocumentCallbackType TYPE, int LINE, int INDEX, String ADDITION, int REMOVAL_LENGTH) {
        this.TYPE = TYPE;
        this.LINE = LINE;
        this.INDEX = INDEX;
        this.ADDITION = ADDITION;
        this.REMOVAL_LENGTH = REMOVAL_LENGTH;
    }
}
