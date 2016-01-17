
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

/**
 * A panel with a CardLayout that manages the many different views of Flow
 * 
 * @author Vince Ou
 *
 */
/**
 * @author Vince
 *
 */
@SuppressWarnings("serial")
public class PanelManager extends JPanel {

	private CardLayout			layout;
	private EditPane			editPane;
	private SettingsPane		settingsTabs;
	private LoginPane			loginPane;
	private DebugPane			debugPane;
	private CreateAccountPane	createAccountPane;
	private HistoryPane			historyPane;

	private EditTabs			editTabs;
	
	private FlowClient			frame;

	/**
	 * Creates a new PanelManager
	 * 
	 * @param frame
	 *        the JFrame
	 */
	public PanelManager(FlowClient frame) {
		// Swing necessities
		layout = new CardLayout();
		this.frame = frame;
		this.setLayout(layout);
		setBorder(FlowClient.EMPTY_BORDER);

		// Creates new panels and adds them to the cardlayout stack
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

	/**
	 * Switches to the editor
	 */
	public void switchToEditor() {
		editPane.addEditTabs(editTabs);
		layout.show(this, "editPane");
		revalidate();
		repaint();
	}

	/**
	 * Switches to the login view
	 */
	public void switchToLogin() {
		layout.show(this, "loginPane");
		loginPane.resetPassFields();
		revalidate();
		repaint();
	}

	/**
	 * Switches to the "Create account" view
	 */
	public void switchToCreateAccount() {
		layout.show(this, "createPane");
		revalidate();
		repaint();
	}

	/**
	 * Switches to the debug view
	 */
	public void switchToDebug() {
		debugPane.addEditTabs(editTabs);
		layout.show(this, "debugPane");
		revalidate();
		repaint();
	}

	/**
	 * Switches to the settings view
	 */
	public void switchToSettings() {
		layout.show(this, "settingsPane");
		revalidate();
		repaint();
	}

	/**
	 * Switches to the history view
	 */
	public void switchToHistory() {
		historyPane.addEditTabs(editTabs);
		layout.show(this, "historyPane");
		revalidate();
		repaint();
	}

	/**
	 * Gets the editPane
	 * 
	 * @return the EditPane
	 */
	public EditPane getEditPane() {
		return editPane;
	}

	/**
	 * Gets the version history pane
	 * 
	 * @return the version history pane
	 */
	public HistoryPane getHistoryPane() {
		return historyPane;
	}

	/**
	 * Gets the login pane
	 * 
	 * @return the login pane
	 */
	public LoginPane getLoginPane() {
		return loginPane;
	}

	/**
	 * Resets the UI on logout and close account
	 */
	public void resetUI() {
		frame.resetUI();
	}
}
