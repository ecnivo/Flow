package struct;

import java.io.Serializable;

/**
 * Created by Netdex on 12/24/2015.
 */
public class User implements Serializable {

	private String username;
	private transient String password;

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
