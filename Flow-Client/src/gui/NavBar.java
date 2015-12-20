package gui;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class NavBar extends JPanel {
    public NavBar(PanelManager manager) {
	setLayout(new FlowLayout());

	add(new EditButton(manager));
	add(new DebugButton(manager));
	add(new SettingsButton(manager));
	add(new RunButton(manager));
	add(new StopButton(manager));
    }

    private class EditButton extends JButton {

	private EditButton(PanelManager manager) {
	    try {
		setIcon(new ImageIcon(ImageIO.read(
			new File("images/editButton.png")).getScaledInstance(
			FlowClient.BUTTON_ICON_SIZE,
			FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
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

	private DebugButton(PanelManager manager) {
	    try {
		setIcon(new ImageIcon(
			ImageIO.read(new File("images/goToDebugButton.png"))
				.getScaledInstance(FlowClient.BUTTON_ICON_SIZE,
					FlowClient.BUTTON_ICON_SIZE,
					Image.SCALE_SMOOTH)));
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
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

	private SettingsButton(PanelManager manager) {
	    try {
		setIcon(new ImageIcon(
			ImageIO.read(new File("images/settingsButton.png"))
				.getScaledInstance(FlowClient.BUTTON_ICON_SIZE,
					FlowClient.BUTTON_ICON_SIZE,
					Image.SCALE_SMOOTH)));
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
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

	public RunButton(PanelManager manager) {
	    try {
		setIcon(new ImageIcon(ImageIO.read(
			new File("images/runButton.png")).getScaledInstance(
			FlowClient.BUTTON_ICON_SIZE,
			FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
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

	public StopButton(PanelManager manager) {
	    try {
		setIcon(new ImageIcon(ImageIO.read(
			new File("images/stopButton.png")).getScaledInstance(
			FlowClient.BUTTON_ICON_SIZE,
			FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
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
