package login;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LoginPane extends JPanel {
	private JTextField txtFlow;
	private JTextField txtUsername;
	private JTextField txtPassword;
	private JTextField txtUsername_1;
	private JTextField txtPassword_1;

	public LoginPane() {
		this.setLayout(null);

		txtFlow = new JTextField();
		txtFlow.setText("FLOW");
		txtFlow.setBounds(160, 21, 101, 22);
		this.add(txtFlow);

		txtUsername = new JTextField();
		txtUsername.setText("Username");
		txtUsername.setBounds(167, 84, 86, 20);
		this.add(txtUsername);
		txtUsername.setColumns(10);

		txtPassword = new JTextField();
		txtPassword.setText("Password");
		txtPassword.setBounds(168, 128, 86, 20);
		this.add(txtPassword);
		txtPassword.setColumns(10);

		txtUsername_1 = new JTextField();
		txtUsername_1.setText("USERNAME");
		txtUsername_1.setBounds(163, 54, 86, 20);
		this.add(txtUsername_1);
		txtUsername_1.setColumns(10);

		txtPassword_1 = new JTextField();
		txtPassword_1.setText("PASSWORD");
		txtPassword_1.setBounds(163, 106, 86, 20);
		this.add(txtPassword_1);
		txtPassword_1.setColumns(10);

		JButton btnLogin = new JButton("Login");
		btnLogin.setBounds(160, 159, 89, 23);
		this.add(btnLogin);

		JButton btnNoAccountCreate = new JButton("No Account? Create one!");
		btnNoAccountCreate.setBounds(163, 217, 140, 46);
		this.add(btnNoAccountCreate);

	}
}
