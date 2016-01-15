package callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Netdex on 1/15/2016.
 */
public class DocumentCallbackManager extends PersistentCallbackManager {

    private static DocumentCallbackManager instance;

    public static DocumentCallbackManager getInstance() {
        if (instance == null)
            instance = new DocumentCallbackManager();
        return instance;
    }

    private HashMap<UUID, ArrayList<DocumentCallbackHandle>> registeredCallbacks;

    private DocumentCallbackManager() {
        this.registeredCallbacks = new HashMap<>();
    }

    @Override
    public void onGlobalEvent(CallbackEvent event) {
        if (!(event instanceof DocumentCallbackEvent))
            throw new IllegalArgumentException("event must be document callback event!");
        DocumentCallbackEvent dce = (DocumentCallbackEvent) event;

    }

    @Override
    public boolean registerPersistentClientHandle(PersistentClientHandle pch) {
        if (!(pch instanceof DocumentCallbackHandle))
            throw new IllegalArgumentException("handle must be a document callback handle!");
        DocumentCallbackHandle dch = (DocumentCallbackHandle) pch;
        if (!this.registeredCallbacks.containsKey(dch.getDocumentUUID()))
            this.registeredCallbacks.put(dch.getDocumentUUID(), new ArrayList<DocumentCallbackHandle>());
        this.registeredCallbacks.get(dch.getDocumentUUID()).add(dch);
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean unregisterPersistentClientHandle(PersistentClientHandle pch) {
        throw new UnsupportedOperationException();
    }
}
