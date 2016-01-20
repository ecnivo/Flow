package struct;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

/**
 * Represents a user
 * Created by Gordon Guan on 12/24/2015.
 */
public class User implements Serializable {

    private static final transient ImageIcon DEFAULT_AVATAR = new ImageIcon(
            new ImageIcon("images/icon.png").getImage().getScaledInstance(32,
                    32, Image.SCALE_SMOOTH));
    private final String username;
    private final transient String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username) {
        this(username, null);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
