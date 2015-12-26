package login;

import gui.BackButton;
import gui.PanelManager;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;

public class CreateAccountPane extends JPanel {
    private PanelManager manager;
    private JPasswordField passwordField;

    // public CreateAccountPane(PanelManager manager, PackageSocket
    // packageSender) {
    public CreateAccountPane(PanelManager manager) {
	setBackground(Color.WHITE);
	this.manager = manager;
	// TODO put a limit on name length
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
		// if (usernameEntry.getText().length() > 16) {
		// JOptionPane
		// .showConfirmDialog(
		// null,
		// "Username is too long.\nUsernames must be below 16 characters.",
		// "Invalid username",
		// JOptionPane.DEFAULT_OPTION,
		// JOptionPane.ERROR_MESSAGE);
		// return;
		// }
		//
		// Data userData = new Data("user");
		// userData.put("user_type", "REGISTER");
		// userData.put("username", usernameEntry.getText());
		// userData.put("password", passwordEntry.getPassword());
		// Data reply = null;
		// try {
		// packageSender.sendPackage(userData);
		// reply = packageSender.receivePackage(Data.class);
		// } catch (IOException e1) {
		// e1.printStackTrace();
		// } catch (ClassNotFoundException e1) {
		// e1.printStackTrace();
		// }
		// String replyMsg = reply.get("status", String.class);
		// switch (replyMsg) {
		// case "USERNAME_TAKEN":
		// JOptionPane
		// .showConfirmDialog(
		// null,
		// "This username has been taken already.\nPlease select another one.",
		// "Username in use",
		// JOptionPane.DEFAULT_OPTION,
		// JOptionPane.ERROR_MESSAGE);
		// return;
		// case "USERNAME_INVALID":
		// JOptionPane
		// .showConfirmDialog(
		// null,
		// "This username is invalid.\nThe most likely cause is the presence of special characters.\nPlease select another one.",
		// "Username invalid",
		// JOptionPane.DEFAULT_OPTION,
		// JOptionPane.ERROR_MESSAGE);
		// return;
		// case "PASSWORD_INVALID":
		// JOptionPane
		// .showConfirmDialog(
		// null,
		// "This password is invalid.\nThe most likely cause is the presence of special characters.\nPlease select another one.",
		// "Password invalid",
		// JOptionPane.DEFAULT_OPTION,
		// JOptionPane.ERROR_MESSAGE);
		// return;
		// }
		// JOptionPane.showConfirmDialog(null,
		// "You have successfully registered a Flow account!",
		// "Registration successful", JOptionPane.DEFAULT_OPTION,
		// JOptionPane.INFORMATION_MESSAGE);
		CreateAccountPane.this.manager.switchToEditor();
	    }
	});
    }
}
