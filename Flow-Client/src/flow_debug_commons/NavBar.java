package flow_debug_commons;

import gui.FlowClient;
import gui.PanelManager;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

public class NavBar extends JToolBar {

    private PanelManager manager;

    public static final byte EDIT = 71;
    public final static byte DEBUG = -18;

    private EditButton editButton;
    private DebugButton debugButton;

    public NavBar(PanelManager panMan) {
	manager = panMan;
	setLayout(new FlowLayout());

	editButton = new EditButton();
	debugButton = new DebugButton();

	add(editButton);
	add(debugButton);
	add(new SettingsButton());
	addSeparator();

	add(new RunButton());
	add(new StopButton());
	addSeparator();

	setFloatable(false);
	setRollover(true);
    }

    public void disableButton(byte button) {
	switch (button) {
	case EDIT:
	    editButton.setEnabled(false);
	    return;
	case DEBUG:
	    debugButton.setEnabled(false);
	    return;
	}
    }

    private class EditButton extends JButton {

	private EditButton() {
	    setToolTipText("Switch to the editing view");
	    try {
		setIcon(new ImageIcon(ImageIO.read(
			new File("images/editWindow.png")).getScaledInstance(
			FlowClient.BUTTON_ICON_SIZE,
			FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	    setFocusable(false);
	    setBorder(FlowClient.EMPTY_BORDER);
	    addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
		    manager.switchToEditor();
		}
	    });
	}
    }

    private class DebugButton extends JButton {

	private DebugButton() {
	    setToolTipText("Switch to the debug view");
	    try {
		setIcon(new ImageIcon(ImageIO.read(
			new File("images/debugWindow.png")).getScaledInstance(
			FlowClient.BUTTON_ICON_SIZE,
			FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
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

    private class SettingsButton extends JButton {

	private SettingsButton() {
	    setToolTipText("Switch to the settings view");
	    try {
		setIcon(new ImageIcon(
			ImageIO.read(new File("images/settingsWindow.png"))
				.getScaledInstance(FlowClient.BUTTON_ICON_SIZE,
					FlowClient.BUTTON_ICON_SIZE,
					Image.SCALE_SMOOTH)));
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

    private class RunButton extends JButton {
	public RunButton() {
	    setToolTipText("Compiles, then runs the file currently open in the editor");
	    try {
		setIcon(new ImageIcon(
			ImageIO.read(new File("images/run.png"))
				.getScaledInstance(FlowClient.BUTTON_ICON_SIZE,
					FlowClient.BUTTON_ICON_SIZE,
					Image.SCALE_SMOOTH)));
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	    setFocusable(false);
	    setBorder(FlowClient.EMPTY_BORDER);
	    addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
		    // TODO make it run
		    System.out.println("Run button pressed");
		}
	    });
	}

    }

    private class StopButton extends JButton {

	public StopButton() {
	    setToolTipText("Stops the currently running program");
	    try {
		setIcon(new ImageIcon(
			ImageIO.read(new File("images/stop.png"))
				.getScaledInstance(FlowClient.BUTTON_ICON_SIZE,
					FlowClient.BUTTON_ICON_SIZE,
					Image.SCALE_SMOOTH)));
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	    setFocusable(false);
	    setBorder(FlowClient.EMPTY_BORDER);
	    addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
		    // TODO make it stop
		    System.out.println("Stop button pressed");
		}
	    });
	}
    }
}
