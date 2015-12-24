package gui;

import history.HistoryPane;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import login.CreateAccountPane;
import login.LoginPane;
import settings.SettingsPane;
import debug.DebugPane;
import editing.EditPane;

public class PanelManager extends JPanel {
    private EditPane editPane;
    private SettingsPane settingsPane;
    private LoginPane loginPane;
    private DebugPane debugPane;
    private CreateAccountPane createAccountPane;
    private CardLayout layout;
    private JFrame frame;
    private HistoryPane historyPane;

    public PanelManager(JFrame frame) {
	layout = new CardLayout();
	this.setLayout(layout);

	loginPane = new LoginPane(this);
	add(loginPane, "loginPane");

	createAccountPane = new CreateAccountPane(this);
	add(createAccountPane, "createPane");

	editPane = new EditPane(this);
	add(editPane, "editPane");

	debugPane = new DebugPane(this);
	add(debugPane, "debugPane");

	settingsPane = new SettingsPane(this);
	add(settingsPane, "settingsPane");

	historyPane = new HistoryPane(this);
	add(historyPane, "historyPane");
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

    public void switchToHistory() {
	layout.show(this, "historyPane");
	revalidate();
	repaint();
    }

    public LoginPane getLoginPane() {
	return loginPane;
    }

    public EditPane getEditPane() {
	return editPane;
    }

    public void setTitleBarName(String newName) {
	frame.setTitle(newName);
    }
}
