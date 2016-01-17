
package login;

import gui.PanelManager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import message.Data;
import shared.Communicator;

/**
 * The pane for the user to enter their credentials to log in with
 * 
 * @author Vince Ou
 *
 */
public class LoginPane extends JPanel {

	private PanelManager	panMan;
	private JPasswordField	passwordEntry;
	private JButton			loginButton;

	// Pan Man! https://i.imgur.com/19iZW9K.png

	/**
	 * Creates a new LoginPane
	 * 
	 * @param panMan
	 *        the associated PanelManager
	 */
	public LoginPane(PanelManager panMan) {
		setBackground(Color.WHITE);

		this.panMan = panMan;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		java.awt.Component verticalStrut_4 = Box.createVerticalStrut(20);
		add(verticalStrut_4);
		// TODO add background picture

		// Sets the title
		JLabel title = new JLabel();
		title.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		add(title);
		try {
			title.setIcon(new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/flow.png")).getScaledInstance(414, 128, Image.SCALE_SMOOTH)));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		java.awt.Component verticalStrut_3 = Box.createVerticalStrut(20);
		add(verticalStrut_3);

		// Asks user for their username
		JLabel usernamePrompt = new JLabel("Username");
		add(usernamePrompt);
		usernamePrompt.setSize(128, 28);
		usernamePrompt.setAlignmentX(CENTER_ALIGNMENT);

		// Creates a place for the user to enter their name
		UsernameBox usernameEntry = new UsernameBox();
		usernameEntry.setMaximumSize(new Dimension(128, 24));
		add(usernameEntry);

		java.awt.Component verticalStrut = Box.createVerticalStrut(20);
		add(verticalStrut);

		// Asks user for password
		JLabel passwordPrompt = new JLabel("Password");
		add(passwordPrompt);
		passwordPrompt.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

		// Lets the user enter their password
		passwordEntry = new JPasswordField();
		passwordEntry.setMaximumSize(new Dimension(128, 24));
		add(passwordEntry);
		passwordEntry.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				// nothing
			}

			// Clears it when you click on it
			@Override
			public void focusGained(FocusEvent e) {
				passwordEntry.setText("");
			}
		});
		passwordEntry.setToolTipText("Your Flow password");
		passwordEntry.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
				// nothing
			}

			/**
			 * Shortcut
			 */
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ENTER)
					loginButton.doClick();
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// nothing
			}
		});

		java.awt.Component verticalStrut_1 = Box.createVerticalStrut(20);
		add(verticalStrut_1);

		// For the user to log in
		loginButton = new JButton("Login");
		add(loginButton);
		loginButton.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

		java.awt.Component verticalStrut_2 = Box.createVerticalStrut(20);
		add(verticalStrut_2);

		// Takes the user to the CreatAccountPane
		JButton createAccountButton = new JButton("<html>No Account?<br>Create one!</html>");
		createAccountButton.setPreferredSize(new Dimension(128, 32));
		createAccountButton.setMinimumSize(new Dimension(32, 2));
		createAccountButton.setMaximumSize(new Dimension(128, 32));
		add(createAccountButton);
		createAccountButton.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		createAccountButton.addActionListener(new ActionListener() {

			/**
			 * Switches to the appropriate window using the PanelManager
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				LoginPane.this.panMan.switchToCreateAccount();
			}
		});
		// Logs in the user...
		loginButton.addActionListener(new ActionListener() {

			/**
			 * Checks that things are in order, then sends a login request to the server
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				// Usernames are limited to 16 characters
				if (usernameEntry.getText().trim().length() > 16) {
					JOptionPane.showConfirmDialog(null, "The username is too long.\nUsernames have a limit of 16 characters.", "Invalid username", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
					return;
				} else if (usernameEntry.getText().trim().equals("Username")) {
					JOptionPane.showConfirmDialog(null, "Please enter a username.", "Invalid username", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
					return;
				}

				// Creates a message to the server
				Data usernamePass = new Data("login");
				usernamePass.put("username", usernameEntry.getText().trim());
				usernamePass.put("password", String.copyValueOf(passwordEntry.getPassword()));

				// Checks if server is online
				Data reply = Communicator.communicate(usernamePass);
				if (reply == null) {
					JOptionPane.showConfirmDialog(null, "The server is currently offline. Please try again at another time.", "Server under maintenance", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
					return;
				}
				// If the server can be contacted...
				String status = reply.get("status", String.class);
				switch (status) {
				// Failure cases
					case "USERNAME_DOES_NOT_EXIST":
						JOptionPane.showConfirmDialog(null, "The username does not exist.\nPlease enter a username that is valid, or create a new account.", "Invalid username", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
						return;
					case "PASSWORD_INCORRECT":
						JOptionPane.showConfirmDialog(null, "Whoops! Your password does not match the one we don't have. Try again.", "Incorrect password", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
						return;
					case "INVALID_CREDENTIALS":
						JOptionPane.showConfirmDialog(null, "Whoops! Your Your credentials are incorrect.\nTry again.", "Invalid credentials", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
						return;
					case "USER_ALREADY_LOGGED_IN":
						JOptionPane.showConfirmDialog(null, "You are logged into your Flow account from another computer.\nPlease log out of that account before you try to log in here.", "Simultaneous logins are not supported", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
						return;
					case "ACCESS_DENIED":
						JOptionPane.showConfirmDialog(null, "You do not have sufficient permissions complete this operation.", "Access Denied", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
						break;
						// Success case
					case "OK":
						// Switching, clearing, and resetting UI
						LoginPane.this.panMan.switchToEditor();
						UUID sessionID = reply.get("session_id", UUID.class);
						Communicator.setSessionID(sessionID);
						Communicator.initAsync(sessionID);
						LoginPane.this.panMan.getEditPane().getFileTree().refreshProjectList();
						LoginPane.this.panMan.getEditPane().getFileTree().expandRow(0);
						LoginPane.this.panMan.getHistoryPane().getTree().refreshProjectList();
						LoginPane.this.panMan.getHistoryPane().getTree().expandRow(0);
						Communicator.setUsername(usernameEntry.getText().trim());
						return;
					default:
						return;
				}
			}
		});
	}

	/**
	 * Resets the password field
	 */
	public void resetPassFields() {
		passwordEntry.setText("");
	}
}
