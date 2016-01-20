package callback;

import java.io.IOException;

/**
 * Represents a server handler that handles a callback event
 * <p>
 * Created by Gordon Guan on 1/15/2016.
 */
class CallbackHandler {

    private final PersistentClientHandle handle;
    private final CallbackEvent.CallbackEventType type;

    CallbackHandler(PersistentClientHandle handle) {
        this.handle = handle;
        this.type = CallbackEvent.CallbackEventType.DOCUMENT_CALLBACK;
    }

    public PersistentClientHandle getHandle() {
        return handle;
    }

    /**
     * An abstract callback that will be handled
     *
     * @param event The event argument passed on
     */
    public void onCallbackEvent(CallbackEvent event) throws IOException {

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
