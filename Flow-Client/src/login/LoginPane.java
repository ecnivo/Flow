package login;

import gui.PanelManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LoginPane extends JPanel {
	private JTextField txtFlow;
	private JTextField txtUsername;
	private JTextField txtPassword;
	private JTextField txtUsername_1;
	private JTextField txtPassword_1;
	private JButton newAccountButton;
	private JButton loginButton;
	private AuthenticationPanelManager authPanMan;

	public LoginPane(AuthenticationPanelManager authPanMan) {
		this.authPanMan = authPanMan;
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

		loginButton = new JButton("Login");
		loginButton.setBounds(160, 159, 89, 23);
		this.add(loginButton);

		newAccountButton = new JButton("No Account? Create one!");
		newAccountButton.setBounds(163, 217, 140, 46);
		newAccountButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				LoginPane.this.switchToNewAccount();
			}
		});
		this.add(newAccountButton);

	}

	private void switchToNewAccount() {
		this.authPanMan.switchToCreate();
	}
}
