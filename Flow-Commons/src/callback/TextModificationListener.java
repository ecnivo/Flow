package callback;

/**
 * Created by Netdex on 1/16/2016.
 */
public class TextModificationListener extends CallbackListener {

    public TextModificationListener() {

    }

    public void onDocumentUpdate(DocumentCallbackEvent event) {

    }

    @Override
    public void onCallbackEvent(CallbackEvent event) {
        onDocumentUpdate((DocumentCallbackEvent) event);
    }
}
