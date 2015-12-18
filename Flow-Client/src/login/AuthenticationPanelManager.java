package login;

import javax.swing.*;
import java.awt.*;

public class AuthenticationPanelManager extends JPanel {

	private LoginPane loginPane;
	private CreateAccountPane createAccountPane;

	private CardLayout layout;

	public AuthenticationPanelManager() {
		super();
		layout = new CardLayout();
		this.setLayout(layout);

		loginPane = new LoginPane(this);
		createAccountPane = new CreateAccountPane();

		this.add(loginPane, "loginPane");
		this.add(createAccountPane, "createAccountPane");

		layout.show(this, "loginPane");
	}

	public void switchToCreate() {
		layout.show(this, "createAccountPane");
	}
}
