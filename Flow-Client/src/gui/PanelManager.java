package gui;

import java.awt.CardLayout;

import javax.swing.JPanel;

import login.CreateAccountPane;
import login.LoginPane;
import settings.SettingsPane;
import editing.EditPane;

public class PanelManager extends JPanel {
    private EditPane editPane;
    private SettingsPane settingsPane;
    private LoginPane loginPane;
    private CreateAccountPane createPane;
    private CardLayout layout;

    public PanelManager() {
	layout = new CardLayout();
	this.setLayout(layout);

	loginPane = new LoginPane(this);
	this.add(loginPane, "loginPane");

	createPane = new CreateAccountPane(this);
	this.add(createPane, "createPane");

	editPane = new EditPane();
	this.add(editPane, "editPane");

	settingsPane = new SettingsPane();
	this.add(settingsPane, "settingsPane");
    }

    public void switchToEditor() {
	layout.show(this, "editPane");
    }

    public void switchToLogin() {
	layout.show(this, "loginPane");
    }

    public void switchToCreateAccount() {
	layout.show(this, "createPane");
    }

    protected void updatePositions(int width, int height) {
	// TODO update login and auth positions
    }

    public LoginPane getLoginPane() {
	return loginPane;
    }
}
