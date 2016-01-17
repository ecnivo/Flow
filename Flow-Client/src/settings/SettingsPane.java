
package settings;

import gui.PanelManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import shared.NavBar;
/**
 * Pane with the title holding the SettingsTabs
 * @author Vince Ou
 *
 */
@SuppressWarnings("serial")
public class SettingsPane extends JPanel {

	// private static final Dimension TEXT_BOX_SIZE = new Dimension(256, 24);
	// private final static Dimension BUTTON_SIZE = new Dimension(128, 32);
	private static final int	SETTINGS_WIDTH	= 550;

	/**
	 * Creates a new SettingsPane
	 * @param panMan the associated PanelManager
	 */
	public SettingsPane(PanelManager panMan) {
		// Swing stuff
		setLayout(new BorderLayout(0, 0));
		setMaximumSize(new Dimension(SETTINGS_WIDTH, Integer.MAX_VALUE));
		// adds the SettingsTabs in the middle
		add(new SettingsTabs(panMan), BorderLayout.CENTER);
		
		// adds a header with a nav bar and a title
		JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		NavBar navBar = new NavBar(panMan);
		navBar.disableButton(NavBar.SETTINGS);
		header.add(navBar);
		JLabel title = new JLabel("Settings");
		title.setFont(new Font("Arial", Font.BOLD, 18));
		header.add(title);
		add(header, BorderLayout.NORTH);
	}
}
