package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Netdex on 1/15/2016.
 */
public class PersistentCallbackManager {
    private static PersistentCallbackManager instance;

    public static PersistentCallbackManager getInstance() {
        if (instance == null)
            instance = new PersistentCallbackManager();
        return instance;
    }

    private HashMap<UUID, ArrayList<PersistentClientHandle>> registeredListeners;

    private PersistentCallbackManager() {
        this.registeredListeners = new HashMap<>();
    }

    public void registerPersistentClientHandle(PersistentClientHandle pch) {

    }

}
