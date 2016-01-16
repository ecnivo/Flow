package callback;

/**
 * Created by Netdex on 1/15/2016.
 */
public abstract class CallbackHandler {

    private PersistentClientHandle handle;

    public CallbackHandler(PersistentClientHandle handle) {
        this.handle = handle;
    }

    public PersistentClientHandle getHandle() {
        return handle;
    }

    public abstract void onCallbackEvent(CallbackEvent event);
}
