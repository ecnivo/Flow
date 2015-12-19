package gui;

import java.awt.CardLayout;

import javax.swing.JPanel;

import login.AuthenticationPanelManager;
import settings.SettingsPane;
import editing.EditPane;

public class PanelManager extends JPanel {
    private EditPane editPanel;
    private SettingsPane settingsPanel;
    private AuthenticationPanelManager loginAuthPanel;
    private CardLayout layout;

    public PanelManager() {
	layout = new CardLayout();
	this.setLayout(layout);

	loginAuthPanel = new AuthenticationPanelManager(this);
	this.add(loginAuthPanel, "loginPanel");

	editPanel = new EditPane();
	this.add(editPanel, "editPanel");

	settingsPanel = new SettingsPane();
	this.add(settingsPanel, "settingsPanel");
    }

    public void switchToEditor() {
	layout.show(this, "editPanel");
    }
}
