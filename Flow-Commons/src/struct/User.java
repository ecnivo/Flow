package struct;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Netdex on 12/24/2015.
 */
public class User implements Serializable {

    private String username;
    private transient String password;
    private ImageIcon avatar;

    private static final ImageIcon DEFAULT_AVATAR = new ImageIcon(
            new ImageIcon("images/icon.png").getImage().getScaledInstance(32,
                    32, Image.SCALE_SMOOTH));

    public User(String username, String password, ImageIcon avatar) {
        this.username = username;
        this.password = password;
        this.avatar = new ImageIcon(avatar.getImage().getScaledInstance(32, 32,
                Image.SCALE_SMOOTH));
    }

    public User(String username, String password) {
        this(username, password, DEFAULT_AVATAR);
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

    public ImageIcon getAvatar() {
        return avatar;
    }

//    public void setAvatar(ImageIcon icon) {
//	this.avatar = new ImageIcon(avatar.getImage().getScaledInstance(32, 32,
//		Image.SCALE_SMOOTH));
//
//    }
}
