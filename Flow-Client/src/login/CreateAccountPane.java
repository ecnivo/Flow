package login;

import javax.swing.*;
import java.awt.*;

public class CreateAccountPane extends JPanel {
	private JTextField txtUsername;
	private JPasswordField passwordField;

	public CreateAccountPane() {
		setLayout(null);

		JLabel lblNewAccount = new JLabel("New Account");
		lblNewAccount.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewAccount.setBounds(64, 40, 124, 27);
		add(lblNewAccount);

		txtUsername = new JTextField();
		txtUsername.setToolTipText("The username you intend to use for Flow");
		txtUsername.setText("Username");
		txtUsername.setBounds(64, 113, 124, 20);
		add(txtUsername);
		txtUsername.setColumns(10);

		JLabel lblUsername = new JLabel("<html>Username<br>(you cannot change this!)</html>");
		lblUsername.setBounds(64, 78, 124, 30);
		add(lblUsername);

		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(64, 162, 124, 14);
		add(lblPassword);

		passwordField = new JPasswordField();
		passwordField.setToolTipText("The password for Flow");
		passwordField.setBounds(64, 187, 124, 20);
		add(passwordField);

		JButton btnSignUp = new JButton("Sign Up!");
		btnSignUp.setBounds(64, 240, 86, 23);
		add(btnSignUp);
		// TODO Auto-generated constructor stub
	}
}
