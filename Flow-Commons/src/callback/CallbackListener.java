package callback;

/**
 * A listener that a callback can call
 * Created by Gordon Guan on 1/16/2016.
 */
public abstract class CallbackListener {

    /**
     * All callback listeners are notified when a callback happens by this event.
     *
     * @param event Metadata pertaining to the evebt
     */
    public abstract void onCallbackEvent(CallbackEvent event);
}
