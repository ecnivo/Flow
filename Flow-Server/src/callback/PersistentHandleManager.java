package callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Netdex on 1/15/2016.
 */
public class PersistentHandleManager {
    private static PersistentHandleManager instance;

    public static PersistentHandleManager getInstance() {
        if (instance == null)
            instance = new PersistentHandleManager();
        return instance;
    }

    private HashMap<UUID, ArrayList<CallbackHandler>> events;

    private PersistentHandleManager() {

    }
}
