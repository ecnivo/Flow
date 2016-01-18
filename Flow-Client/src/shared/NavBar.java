
package shared;

import gui.FlowClient;
import gui.PanelManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Navigation bar for edit/debug/version history/settings
 * 
 * @author Vince Ou
 *
 */
public class NavBar extends JToolBar {

	private PanelManager manager;

	// Because why not.
	public static final byte EDIT = 71;
	public final static byte DEBUG = -18;
	public static final byte HISTORY = 0;
	public static final byte SETTINGS = -35;

	// Various buttons.
	private EditButton editButton;
	private DebugButton debugButton;
	private HistoryButton historyButton;
	private SettingsButton settingsButton;

	/**
	 * Creates a new NavBar
	 * 
	 * @param panMan
	 *        the associated PanelManager
	 */
	public NavBar(PanelManager panMan) {
		// Swing setup
		manager = panMan;
		setBorder(FlowClient.EMPTY_BORDER);
		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		// Creates the buttons
		editButton = new EditButton();
		debugButton = new DebugButton();
		historyButton = new HistoryButton();
		settingsButton = new SettingsButton();

		// adds the buttons
		add(editButton);
		if (!FlowClient.HIDE) {
			add(debugButton);
			add(historyButton);
		}
		add(settingsButton);
		addSeparator();

		// More swing stuff
		setFloatable(false);
		setRollover(true);
	}

	/**
	 * Disable a button for a particular pane
	 * 
	 * @param button
	 *        the button to disable
	 */
	public void disableButton(byte button) {
		// Set disables whichever button
		switch (button) {
			case EDIT:
				editButton.setEnabled(false);
				return;
			case DEBUG:
				if (!FlowClient.HIDE)
					debugButton.setEnabled(false);
				return;
			case HISTORY:
				if (!FlowClient.HIDE)
					historyButton.setEnabled(false);
				return;
			case SETTINGS:
				settingsButton.setEnabled(false);
				return;
		}
	}

	/**
	 * Button to navigate the user to the Edit pane
	 * 
	 * @author Vince Ou
	 *
	 */
	private class EditButton extends JButton {

		private EditButton() {
			setToolTipText("Switch to the editing view");
			setBorder(FlowClient.EMPTY_BORDER);
			// Loads icon
			try {
				setIcon(new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/editWindow.png")).getScaledInstance(FlowClient.BUTTON_ICON_SIZE, FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			setFocusable(false);
			setBorder(FlowClient.EMPTY_BORDER);
			addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// Switches panes when needed
					manager.switchToEditor();
				}
			});
		}
	}

	/**
	 * Button to navigate the user to the Debug pane. Near identical copy of EditButton
	 * 
	 * @author Vince
	 *
	 */
	private class DebugButton extends JButton {

		private DebugButton() {
			setToolTipText("Switch to the debug view");
			setBorder(FlowClient.EMPTY_BORDER);
			try {
				setIcon(new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/debugWindow.png")).getScaledInstance(FlowClient.BUTTON_ICON_SIZE, FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			setFocusable(false);
			setBorder(FlowClient.EMPTY_BORDER);
			addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					manager.switchToDebug();
				}
			});
		}
	}

	/**
	 * Button to navigate the user to the History pane. Near identical copy of EditButton
	 * 
	 * @author Vince
	 *
	 */
	private class HistoryButton extends JButton {

		private HistoryButton() {
			setToolTipText("Switch to the version history view");
			setBorder(FlowClient.EMPTY_BORDER);
			try {
				setIcon(new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/historyWindow.png")).getScaledInstance(FlowClient.BUTTON_ICON_SIZE, FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			setFocusable(false);
			setBorder(FlowClient.EMPTY_BORDER);
			addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					manager.switchToHistory();
					// TODO if the last open window is the editor, then get the
					// currently open file in the editor and open it in the
					// debug's tab view, and switch to that tab.
				}
			});
		}
	}

	/**
	 * Button to navigate the user to the Settings pane. Near identical copy of EditButton
	 * 
	 * @author Vince
	 *
	 */
	private class SettingsButton extends JButton {

		private SettingsButton() {
			setToolTipText("Switch to the settings view");
			setBorder(FlowClient.EMPTY_BORDER);
			try {
				setIcon(new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/settingsWindow.png")).getScaledInstance(FlowClient.BUTTON_ICON_SIZE, FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			setFocusable(false);
			setBorder(FlowClient.EMPTY_BORDER);
			addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					manager.switchToSettings();
				}
			});
		}
	}

}
