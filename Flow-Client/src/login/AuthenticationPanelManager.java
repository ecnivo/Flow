package login;

import gui.PanelManager;

import java.awt.CardLayout;

import javax.swing.JPanel;

public class AuthenticationPanelManager extends JPanel {

    private LoginPane loginPane;
    private CreateAccountPane createAccountPane;
    private PanelManager manager;
    private CardLayout layout;

    public AuthenticationPanelManager(PanelManager manager) {
	this.manager = manager;
	layout = new CardLayout();
	this.setLayout(layout);

	loginPane = new LoginPane(this);
	createAccountPane = new CreateAccountPane(this);

	this.add(loginPane, "loginPane");
	this.add(createAccountPane, "createAccountPane");

	layout.show(this, "loginPane");
    }

    public void switchToCreateNewAccount() {
	layout.show(this, "createAccountPane");
    }

    public void switchToEditor() {
	manager.switchToEditor();
    }
}
