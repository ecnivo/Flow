package gui;

import history.HistoryPane;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import login.CreateAccountPane;
import login.LoginPane;
import settings.SettingsPane;
import shared.EditTabs;
import debug.DebugPane;
import editing.EditPane;

@SuppressWarnings("serial")
public class PanelManager extends JPanel {
    private CardLayout layout;
    private EditPane editPane;
    private SettingsPane settingsTabs;
    private LoginPane loginPane;
    private DebugPane debugPane;
    private CreateAccountPane createAccountPane;
    private HistoryPane historyPane;

    private EditTabs editTabs;

    public PanelManager(JFrame frame) {
	layout = new CardLayout();
	this.setLayout(layout);
	setBorder(FlowClient.EMPTY_BORDER);

	editTabs = new EditTabs();

	loginPane = new LoginPane(this);
	add(loginPane, "loginPane");

	createAccountPane = new CreateAccountPane(this);
	add(createAccountPane, "createPane");

	editPane = new EditPane(this);
	add(editPane, "editPane");

	debugPane = new DebugPane(this);
	add(debugPane, "debugPane");

	settingsTabs = new SettingsPane(this);
	add(settingsTabs, "settingsPane");

	historyPane = new HistoryPane(this);
	add(historyPane, "historyPane");
    }

    public void switchToEditor() {
	editPane.addEditTabs(editTabs);
	layout.show(this, "editPane");
	revalidate();
	repaint();
    }

    public void switchToLogin() {
	layout.show(this, "loginPane");
	loginPane.resetPassFields();
	revalidate();
	repaint();
    }

    public void switchToCreateAccount() {
	layout.show(this, "createPane");
	revalidate();
	repaint();
    }

    public void switchToDebug() {
	debugPane.addEditTabs(editTabs);
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
	historyPane.addEditTabs(editTabs);
	layout.show(this, "historyPane");
	revalidate();
	repaint();
    }

    public EditPane getEditPane() {
	return editPane;
    }
    
    public HistoryPane getHistoryPane(){
	return historyPane;
    }

    public LoginPane getLoginPane() {
	return loginPane;
    }

    public void resetUI() {
	((DefaultMutableTreeNode) ((DefaultTreeModel) editPane.getDocTree().getModel()).getRoot()).removeAllChildren();
	((DefaultTreeModel) editPane.getDocTree().getModel()).reload();
	((DefaultMutableTreeNode) ((DefaultTreeModel) historyPane.getTree().getModel()).getRoot()).removeAllChildren();
	editTabs = new EditTabs();
    }
}
