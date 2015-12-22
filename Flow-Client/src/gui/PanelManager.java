package gui;

import java.awt.CardLayout;

import javax.swing.JPanel;

import login.CreateAccountPane;
import login.LoginPane;
import settings.SettingsPane;
import debug.DebugPane;
import editing.EditPane;
import flow_debug_commons.NavBar;

public class PanelManager extends JPanel {
    private EditPane editPane;
    private SettingsPane settingsPane;
    private LoginPane loginPane;
    private DebugPane debugPane;
    private CreateAccountPane createAccountPane;
    private CardLayout layout;

    public PanelManager() {
	layout = new CardLayout();
	this.setLayout(layout);

	loginPane = new LoginPane(this);
	add(loginPane, "loginPane");

	createAccountPane = new CreateAccountPane(this);
	add(createAccountPane, "createPane");

	NavBar navBar = new NavBar(this);

	editPane = new EditPane(navBar);
	add(editPane, "editPane");

	debugPane = new DebugPane(editPane, navBar);
	add(debugPane, "debugPane");

	settingsPane = new SettingsPane(this);
	add(settingsPane, "settingsPane");
    }

    public void switchToEditor() {
	layout.show(this, "editPane");
	revalidate();
	repaint();
    }

    public void switchToLogin() {
	layout.show(this, "loginPane");
	revalidate();
	repaint();
    }

    public void switchToCreateAccount() {
	layout.show(this, "createPane");
	revalidate();
	repaint();
    }

    public void switchToDebug() {
	layout.show(this, "debugPane");
	revalidate();
	repaint();
    }

    public void switchToSettings() {
	layout.show(this, "settingsPane");
	revalidate();
	repaint();
    }

    public LoginPane getLoginPane() {
	return loginPane;
    }

    public JPanel getEditPane() {
	return editPane;
    }
}
