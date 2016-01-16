package callback;

/**
 * Represents a handler that handles a callback event
 * <p>
 * Created by Netdex on 1/15/2016.
 */
public class CallbackHandler {

    private PersistentClientHandle handle;
    private CallbackEvent.CallbackEventType type;

    public CallbackHandler(PersistentClientHandle handle, CallbackEvent.CallbackEventType type) {
        this.handle = handle;
        this.type = type;
    }

    public PersistentClientHandle getHandle() {
        return handle;
    }

    /**
     * An abstract callback that will be handled
     *
     * @param event The event argument passed on
     */
    public void onCallbackEvent(CallbackEvent event) {

    }

    /**
     * Called when this callback handler is registered
     *
     * @param event The event argument passed on
     */
    public void onRegister(RegisterEvent event) {

    }

    /**
     * Called when this callback handler is unregistered
     *
     * @param event The event argument passed on
     */
    public void onUnregister(RegisterEvent event) {

    }

    public CallbackEvent.CallbackEventType getType() {
        return type;
    }
}
