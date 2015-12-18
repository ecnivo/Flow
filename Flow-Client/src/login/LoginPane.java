package login;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPane extends JPanel {
	private JTextField txtUsername;
	private JButton newAccountButton;
	private JButton loginButton;
	private AuthenticationPanelManager authPanMan;
	private JLabel lblUsername;
	private JLabel lblPassword;
	private JPasswordField passwordField;

	public LoginPane(AuthenticationPanelManager authPanMan) {
		this.authPanMan = authPanMan;
		this.setLayout(null);

		txtUsername = new JTextField();
		txtUsername.setToolTipText("Your Flow username");
		txtUsername.setText("Username");
		txtUsername.setBounds(165, 91, 165, 20);
		this.add(txtUsername);
		txtUsername.setColumns(10);

		loginButton = new JButton("Login");
		loginButton.setBounds(165, 183, 89, 23);
		this.add(loginButton);

		newAccountButton = new JButton("<html>No Account?<br>Create one!</html>");
		newAccountButton.setBounds(165, 243, 140, 37);
		newAccountButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				LoginPane.this.switchToNewAccount();
			}
		});
		this.add(newAccountButton);

		lblUsername = new JLabel("Username");
		lblUsername.setBounds(165, 64, 165, 16);
		add(lblUsername);

		lblPassword = new JLabel("Password");
		lblPassword.setBounds(165, 127, 165, 16);
		add(lblPassword);

		passwordField = new JPasswordField();
		passwordField.setToolTipText("Your Flow password");
		passwordField.setBounds(165, 154, 165, 20);
		add(passwordField);

	}

	private void switchToNewAccount() {
		this.authPanMan.switchToCreate();
	}
}
