package gui;

import java.awt.CardLayout;

import javax.swing.JPanel;

import login.LoginPane;
import settings.SettingsPane;
import editing.EditPane;

public class PanelManager extends JPanel {
	private EditPane editPanel;
	private SettingsPane settingsPanel;
	private LoginPane loginPanel;

	public PanelManager() {
		this.setLayout(new CardLayout());

		loginPanel = new LoginPane();
		this.add(loginPanel, "loginPanel");
		
		editPanel = new EditPane();
		this.add(editPanel, "editPanel");

		settingsPanel = new SettingsPane();
		this.add(settingsPanel, "settingsPanel");
	}
}
