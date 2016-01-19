package callback;

/**
 * A specific listener for listening to text document modification
 * Created by Gordon Guan on 1/16/2016.
 */
public class TextModificationListener extends CallbackListener {

    public TextModificationListener() {

    }

    /**
     * Called when a document being listened to is modified
     *
     * @param event The event metadata
     */
    public void onDocumentUpdate(DocumentCallbackEvent event) {

    }

    @Override
    public void onCallbackEvent(CallbackEvent event) {
        onDocumentUpdate((DocumentCallbackEvent) event);
    }
}
