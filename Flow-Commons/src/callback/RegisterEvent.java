package callback;

import java.util.UUID;

/**
 * An event called when registering a listener
 * Created by Gordon Guan on 1/16/2016.
 */
public class RegisterEvent {
    public final UUID UUID;
    private final RegisterType TYPE;
    public RegisterEvent(UUID UUID, RegisterType TYPE) {
        this.UUID = UUID;
        this.TYPE = TYPE;
    }

    public enum RegisterType {
        REGISTER,
        UNREGISTER
    }

}
