package gui;

import debug.DebugPane;
import editing.EditPane;
import history.HistoryPane;
import login.CreateAccountPane;
import login.LoginPane;
import settings.SettingsPane;
import shared.EditTabs;

import javax.swing.*;
import java.awt.*;

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

	private static PanelManager instance;

	private final CardLayout layout;
	private final EditPane editPane;
	private final LoginPane loginPane;
	private final DebugPane debugPane;
	private final HistoryPane historyPane;

	private final EditTabs editTabs;

	private final FlowClient frame;

	/**
	 * Creates a new PanelManager
	 * 
	 * @param frame
	 *            the JFrame
	 */
	private PanelManager(FlowClient frame) {
		// Swing necessities
		layout = new CardLayout();
		this.frame = frame;
		this.setLayout(layout);
		setBorder(FlowClient.EMPTY_BORDER);

		// Creates new panels and adds them to the cardlayout stack
		editTabs = new EditTabs();

		loginPane = new LoginPane(this);
		add(loginPane, "loginPane");

		CreateAccountPane createAccountPane = new CreateAccountPane(this);
		add(createAccountPane, "createPane");

		editPane = new EditPane(this);
		add(editPane, "editPane");

		debugPane = new DebugPane(this);
		add(debugPane, "debugPane");

		SettingsPane settingsTabs = new SettingsPane(this);
		add(settingsTabs, "settingsPane");

		historyPane = new HistoryPane(this);
		add(historyPane, "historyPane");
	}

	public static PanelManager getInstance() {
		return instance;
	}

	public static PanelManager createNewInstance(FlowClient frame) {
		instance = new PanelManager(frame);
		return instance;
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
