package login;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

public class LoginPane extends JPanel {
    private AuthenticationPanelManager authPanMan;

    public LoginPane(AuthenticationPanelManager authPanMan) {
	setBackground(Color.WHITE);
	this.authPanMan = authPanMan;
	this.setLayout(null);
	// TODO add background picture

	// TODO make title less gross (logo, possibly?)
	JLabel title = new JLabel("Flow - Log In");
	title.setFont(new Font("Tahoma", Font.BOLD, 18));
	title.setBounds(165, 33, 165, 20);
	this.add(title);

	JLabel usernamePrompt = new JLabel("Username");
	usernamePrompt.setBounds(165, 64, 165, 16);
	this.add(usernamePrompt);

	UsernameBox usernameEntry = new UsernameBox();
	usernameEntry.setBounds(165, 91, 165, 20);
	this.add(usernameEntry);

	JLabel passwordPrompt = new JLabel("Password");
	passwordPrompt.setBounds(165, 127, 165, 16);
	this.add(passwordPrompt);

	JPasswordField passwordEntry = new JPasswordField();
	passwordEntry.setToolTipText("Your Flow password");
	passwordEntry.setBounds(165, 154, 165, 20);
	this.add(passwordEntry);

	JButton logInButton = new JButton("Login");
	logInButton.setBounds(165, 183, 89, 23);
	logInButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		// TODO add something here to send username and password off for
		// authentication
		LoginPane.this.authPanMan.switchToEditor();
	    }
	});
	this.add(logInButton);

	JButton newAccountButton = new JButton(
		"<html>No Account?<br>Create one!</html>");
	newAccountButton.setBounds(165, 243, 140, 37);
	newAccountButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		LoginPane.this.authPanMan.switchToCreateNewAccount();
	    }
	});
	this.add(newAccountButton);
    }
}
