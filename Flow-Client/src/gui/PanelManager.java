package gui;

import java.awt.CardLayout;

import javax.swing.JPanel;

import debug.DebugPane;
import login.CreateAccountPane;
import login.LoginPane;
import settings.SettingsPane;
import editing.EditPane;

public class PanelManager extends JPanel {
    private EditPane editPane;
    private SettingsPane settingsPane;
    private LoginPane loginPane;
    private DebugPane debugPane;
    private CreateAccountPane createAccountPane;
    private NavBar navBar;
    private CardLayout layout;

    public PanelManager() {
	layout = new CardLayout();
	this.setLayout(layout);

	loginPane = new LoginPane(this);
	this.add(loginPane, "loginPane");

	createAccountPane = new CreateAccountPane(this);
	this.add(createAccountPane, "createPane");

	editPane = new EditPane(navBar);
	this.add(editPane, "editPane");

	navBar = new NavBar(this);

	debugPane = new DebugPane(editPane, navBar);
	this.add(debugPane, "debugPane");

	settingsPane = new SettingsPane();
	this.add(settingsPane, "settingsPane");

	layout.show(this, "loginPane");
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

    public void switchToDebug() {
	layout.show(this, "debugPane");
    }
    
    public void switchToSettings(){
	layout.show(this, "settingsPane");
    }

    public LoginPane getLoginPane() {
	return loginPane;
    }
}
