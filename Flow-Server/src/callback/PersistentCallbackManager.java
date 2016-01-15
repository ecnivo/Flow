package callback;

/**
 * Created by Netdex on 1/15/2016.
 */
public abstract class PersistentCallbackManager {

    protected PersistentCallbackManager() {

    }

    public void doGlobalEvent(CallbackEvent event) {
        this.onGlobalEvent(event);
    }

    public abstract void onGlobalEvent(CallbackEvent event);

    public abstract boolean registerPersistentClientHandle(PersistentClientHandle pch);

    public abstract boolean unregisterPersistentClientHandle(PersistentClientHandle pch);

}
