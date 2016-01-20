
package login;

import gui.BackButton;
import gui.PanelManager;
import message.Data;
import shared.Communicator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Arrays;

/**
 * Panel for the user to create a new account
 * 
 * @author Vince Ou
 *
 */
@SuppressWarnings("serial")
public class CreateAccountPane extends JPanel {

	private final PanelManager manager;
	private final JPasswordField passwordConfirm;

	// characters not allowed in... everything, basically.
	public final static char[]	INVALID_CHARS	= { '\\', '/', '?', '%', '*', ':', '|', '"', '<', '>', '#', '&', '{', '}', '$', '@', '=', '`', '+' };

	/**
	 * Creates a new CreateAccountPane
	 * 
	 * @param manager
	 *        the associated PanelManager
	 */
	public CreateAccountPane(PanelManager manager) {
		// Swing setup
		setBackground(Color.WHITE);
		this.manager = manager;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		Component verticalStrut_3 = Box.createVerticalStrut(20);
		add(verticalStrut_3);

		// Title
		JLabel title = new JLabel("New Account");
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		title.setFont(new Font("Tahoma", Font.BOLD, 14));
		add(title);

		Component verticalStrut = Box.createVerticalStrut(20);
		add(verticalStrut);

		// Prompts user for username
		JLabel usernamePrompt = new JLabel("Username (you cannot change this!)");
		usernamePrompt.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(usernamePrompt);

		// Username entry box
		UsernameBox usernameEntry = new UsernameBox();
		usernameEntry.setPreferredSize(new Dimension(128, 24));
		usernameEntry.setMaximumSize(new Dimension(128, 24));
		add(usernameEntry);

		Component verticalStrut_1 = Box.createVerticalStrut(20);
		add(verticalStrut_1);

		// Asks user for password
		JLabel passwordPrompt = new JLabel("Password");
		passwordPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(passwordPrompt);

		// Gets user to enter password
		JPasswordField passwordEntry = new JPasswordField();
		passwordEntry.setMaximumSize(new Dimension(128, 24));
		passwordEntry.setPreferredSize(new Dimension(128, 24));
		passwordEntry.setToolTipText("The password for Flow");
		passwordEntry.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				// nothing
			}

			/**
			 * Resets password field when focus gained (for security)
			 */
			@Override
			public void focusGained(FocusEvent e) {
				passwordEntry.setText("");
			}
		});
		add(passwordEntry);

		Component verticalStrut_4 = Box.createVerticalStrut(20);
		add(verticalStrut_4);

		// Prompts user to type password again
		JLabel label = new JLabel("Re-type password");
		label.setAlignmentX(0.5f);
		add(label);

		// Passwd confirmation box
		passwordConfirm = new JPasswordField();
		passwordConfirm.setToolTipText("The password for Flow");
		passwordConfirm.setPreferredSize(new Dimension(128, 24));
		passwordConfirm.setMaximumSize(new Dimension(128, 24));
		passwordConfirm.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				// nothing
			}

			@Override
			public void focusGained(FocusEvent e) {
				passwordConfirm.setText("");
			}
		});
		add(passwordConfirm);

		Component verticalStrut_2 = Box.createVerticalStrut(20);
		add(verticalStrut_2);

		// Panel at the bottom for a back and register button
		JPanel bottomButtons = new JPanel();
		bottomButtons.setBackground(Color.WHITE);
		add(bottomButtons);
		bottomButtons.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		BackButton backButton = new BackButton(manager.getLoginPane(), manager);
		bottomButtons.add(backButton);
		backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		backButton.setHorizontalAlignment(SwingConstants.LEFT);

		JButton createAccountButton = new JButton("Sign Up!");
		bottomButtons.add(createAccountButton);
		createAccountButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		createAccountButton.addActionListener(new ActionListener() {

			/**
			 * Checks that the username and passwords are in order
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				int usernameLength = usernameEntry.getText().trim().length();
				// Checks that the credentials are all in order. Displays error boxes when
				// necessary.
				if (usernameLength < 1 || usernameLength > 16) {
					JOptionPane.showConfirmDialog(null, "Username is too long or too short.\nUsernames must be no less than 1 character, and no greater than 16 characters.", "Invalid username", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
					return;
				} else if (passwordEntry.getPassword().length < 1) {
					JOptionPane.showConfirmDialog(null, "Please enter a password", "No password", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
					return;
				} else if (stringContains(usernameEntry.getText())) {
					JOptionPane.showConfirmDialog(null, "Username contains invalid characters.\nPlease reduce the use of symbols.", "Invalid username", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
					return;
				} else if (!Arrays.equals(passwordEntry.getPassword(), passwordConfirm.getPassword())) {
					JOptionPane.showConfirmDialog(null, "The passwords do not match; try again.", "Invalid password.", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
					return;
				}

				// Prepares message for server
				Data userData = new Data("user");
				userData.put("user_type", "REGISTER");
				userData.put("username", usernameEntry.getText().trim());
				userData.put("password", String.copyValueOf(passwordEntry.getPassword()));

				// Sends message off to server
				Data reply = Communicator.communicate(userData);
				if (reply == null) {
					JOptionPane.showConfirmDialog(null, "Are you sure the server is running?", "Server is offline", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
					return;
				}
				String replyMsg = reply.get("status", String.class);
				switch (replyMsg) {
				// Failure cases
					case "USERNAME_TAKEN":
						JOptionPane.showConfirmDialog(null, "This username has been taken already.\nPlease select another one.", "Username in use", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
						return;
					case "USERNAME_INVALID":
						JOptionPane.showConfirmDialog(null, "This username is invalid.\nThe most likely cause is the presence of special characters.\nPlease select another one.", "Username invalid", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
						return;
					case "PASSWORD_INVALID":
						JOptionPane.showConfirmDialog(null, "This password is invalid.\nThe most likely cause is the presence of special characters.\nPlease select another one.", "Password invalid", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
						return;
						
					case "ACCESS_DENIED":
						JOptionPane.showConfirmDialog(null, "You do not have sufficient permissions complete this operation.", "Access Denied", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
						break;
						// Success case: clears boxes and switches back to the login pane
					case "OK":
						JOptionPane.showConfirmDialog(null, "Congratulations, you have successfully registered a Flow account!\nEnter your username and password on the next page to login.", "Registration successful", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
						passwordEntry.setText("");
						passwordConfirm.setText("");
						CreateAccountPane.this.manager.switchToLogin();
						break;
				}
			}
		});
	}

	/**
	 * To see if a string contains any of the characters in the array
	 * 
	 * @param array
	 *        the array to reference
	 * @param str
	 *        string to check
	 * @return boolean; true if contains, false if not.
	 */
	public static boolean stringContains(String str) {
		for (char c : CreateAccountPane.INVALID_CHARS) {
			if (str.contains(c + ""))
				return true;
		}
		return false;
	}
}
