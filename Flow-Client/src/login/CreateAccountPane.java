package login;

import gui.BackButton;
import gui.FlowClient;
import gui.PanelManager;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;

import shared.Communicator;
import message.Data;

@SuppressWarnings("serial")
public class CreateAccountPane extends JPanel {
    private PanelManager manager;
    private JPasswordField passwordField;

    public final static char[] INVALID_CHARS = { '\\', '/', '?', '%', '*', ':', '|', '"', '<', '>', '.', '#', '&', '{', '}', '$', '@', '=', '`', '+' };

    public CreateAccountPane(PanelManager manager) {
	setBackground(Color.WHITE);
	this.manager = manager;
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

	Component verticalStrut_3 = Box.createVerticalStrut(20);
	add(verticalStrut_3);

	JLabel title = new JLabel("New Account");
	title.setAlignmentX(Component.CENTER_ALIGNMENT);
	title.setFont(new Font("Tahoma", Font.BOLD, 14));
	add(title);

	Component verticalStrut = Box.createVerticalStrut(20);
	add(verticalStrut);

	JLabel usernamePrompt = new JLabel("Username (you cannot change this!)");
	usernamePrompt.setAlignmentX(Component.CENTER_ALIGNMENT);
	add(usernamePrompt);

	UsernameBox usernameEntry = new UsernameBox();
	usernameEntry.setPreferredSize(new Dimension(128, 24));
	usernameEntry.setMaximumSize(new Dimension(128, 24));
	add(usernameEntry);

	Component verticalStrut_1 = Box.createVerticalStrut(20);
	add(verticalStrut_1);

	JLabel passwordPrompt = new JLabel("Password");
	passwordPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);
	add(passwordPrompt);

	JPasswordField passwordEntry = new JPasswordField();
	passwordEntry.setMaximumSize(new Dimension(128, 24));
	passwordEntry.setPreferredSize(new Dimension(128, 24));
	passwordEntry.setToolTipText("The password for Flow");
	add(passwordEntry);

	Component verticalStrut_4 = Box.createVerticalStrut(20);
	add(verticalStrut_4);

	JLabel label = new JLabel("Re-type password");
	label.setAlignmentX(0.5f);
	add(label);

	passwordField = new JPasswordField();
	passwordField.setToolTipText("The password for Flow");
	passwordField.setPreferredSize(new Dimension(128, 24));
	passwordField.setMaximumSize(new Dimension(128, 24));
	add(passwordField);

	Component verticalStrut_2 = Box.createVerticalStrut(20);
	add(verticalStrut_2);

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

	    @Override
	    public void actionPerformed(ActionEvent e) {
		if (FlowClient.NETWORK) {
		    int usernameLength = usernameEntry.getText().trim().length();
		    if (usernameLength < 1 || usernameLength > 16) {
			JOptionPane.showConfirmDialog(null, "Username is too long or too short.\nUsernames must be no less than 1 character, and no greater than 16 characters.", "Invalid username", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			return;
		    } else if (passwordEntry.getPassword().length < 1) {
			JOptionPane.showConfirmDialog(null, "Please enter a password", "No password", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			return;
		    } else if (stringContains(usernameEntry.getText(), INVALID_CHARS)) {
			JOptionPane.showConfirmDialog(null, "Username contains invalid characters.\nPlease reduce the use of symbols.", "Invalid username", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			return;
		    } else if (!Arrays.equals(passwordEntry.getPassword(), passwordField.getPassword())) {
			JOptionPane.showConfirmDialog(null, "The passwords do not match; try again.", "Invalid password.", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			return;
		    }

		    Data userData = new Data("user");
		    userData.put("user_type", "REGISTER");
		    userData.put("username", usernameEntry.getText().trim());
		    userData.put("password", String.copyValueOf(passwordEntry.getPassword()));

		    String replyMsg = Communicator.communicate(userData).get("status", String.class);
		    switch (replyMsg) {
		    case "USERNAME_TAKEN":
			JOptionPane.showConfirmDialog(null, "This username has been taken already.\nPlease select another one.", "Username in use", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			return;
		    case "USERNAME_INVALID":
			JOptionPane.showConfirmDialog(null, "This username is invalid.\nThe most likely cause is the presence of special characters.\nPlease select another one.", "Username invalid", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			return;
		    case "PASSWORD_INVALID":
			JOptionPane.showConfirmDialog(null, "This password is invalid.\nThe most likely cause is the presence of special characters.\nPlease select another one.", "Password invalid", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			return;
		    }
		}
		JOptionPane.showConfirmDialog(null, "Congratulations, you have successfully registered a Flow account!\nEnter your username and password on the next page to login.", "Registration successful", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
		CreateAccountPane.this.manager.switchToLogin();
	    }
	});
    }

    public static boolean stringContains(String str, char[] array) {
	for (char c : array) {
	    if (str.contains(c + ""))
		return true;
	}
	return false;
    }
}
