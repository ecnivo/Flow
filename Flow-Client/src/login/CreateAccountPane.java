package login;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

public class CreateAccountPane extends JPanel {
    private AuthenticationPanelManager manager;

    public CreateAccountPane(AuthenticationPanelManager manager) {
	this.manager = manager;
	setLayout(null);

	JLabel title = new JLabel("New Account");
	title.setFont(new Font("Tahoma", Font.BOLD, 14));
	title.setBounds(64, 40, 124, 27);
	add(title);

	JLabel usernamePrompt = new JLabel(
		"<html>Username<br>(you cannot change this!)</html>");
	usernamePrompt.setBounds(64, 78, 124, 30);
	add(usernamePrompt);

	UsernameBox usernameEntry = new UsernameBox();
	usernameEntry.setBounds(64, 113, 124, 20);
	add(usernameEntry);

	JLabel passwordPrompt = new JLabel("Password");
	passwordPrompt.setBounds(64, 162, 124, 14);
	add(passwordPrompt);

	JPasswordField passwordEntry = new JPasswordField();
	passwordEntry.setToolTipText("The password for Flow");
	passwordEntry.setBounds(64, 187, 124, 20);
	add(passwordEntry);

	JButton newAccountButton = new JButton("Sign Up!");
	newAccountButton.setBounds(64, 240, 86, 23);
	newAccountButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		// TODO send new user name and password to server to add them to
		// the list
		CreateAccountPane.this.manager.switchToEditor();
	    }
	});
	add(newAccountButton);
    }
}
