package settings;

import gui.NavBar;
import gui.PanelManager;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class SettingsPane extends JPanel {

    public SettingsPane(PanelManager manager) {
	setLayout(new BorderLayout(0, 0));
	add(new SettingsTabs(), BorderLayout.CENTER);
	JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
	NavBar navBar = new NavBar(manager);
	navBar.disableButton(NavBar.SETTINGS);
	header.add(navBar);
	header.add(new JLabel("Settings"));
	add(header, BorderLayout.NORTH);
    }
}
