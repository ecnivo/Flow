package login;

import java.awt.CardLayout;

import javax.swing.JPanel;

public class AuthenticationPanelManager extends JPanel {

	private LoginPane loginPane;
	private CreateAccountPane createAccountPane;

	public AuthenticationPanelManager() {
		this.setLayout(new CardLayout());

		loginPane = new LoginPane(this);
		createAccountPane = new CreateAccountPane();

		this.add(loginPane, "loginPane");
		this.add(createAccountPane, "createAccountPane");
	}

	public void switchToCreate() {

	}
}
