package debug;

import gui.FlowClient;

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

public class DebugToolbar extends JToolBar {
    public DebugToolbar() {
	this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

	addSeparator();
	add(new StartDebuggingButton());
	add(new StepInButton());
	add(new StepOverButton());
	add(new StepOutButton());

	setFloatable(false);

    }

    private class StartDebuggingButton extends JButton {
	private StartDebuggingButton() {
	    setToolTipText("Start debugging the currently open tab");
	    try {
		setIcon(new ImageIcon(
			ImageIO.read(new File("images/debug.png"))
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
		    // TODO Do some stuff to start debugging
		    System.out.println("Debug button pressed!");
		}
	    });
	}
    }

    private class StepInButton extends JButton {
	private StepInButton() {
	    setToolTipText("Step into the current ");
	    try {
		setIcon(new ImageIcon(ImageIO.read(
			new File("images/stepIn.png")).getScaledInstance(
			FlowClient.BUTTON_ICON_SIZE,
			FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	    setBorder(FlowClient.EMPTY_BORDER);
	    addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
		    // TODO step in using debugger
		    System.out.println("Step in button pressed");
		}
	    });
	}
    }

    private class StepOverButton extends JButton {
	private StepOverButton() {
	    try {
		setIcon(new ImageIcon(ImageIO.read(
			new File("images/stepOver.png")).getScaledInstance(
			FlowClient.BUTTON_ICON_SIZE,
			FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	    setBorder(FlowClient.EMPTY_BORDER);
	    addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
		    // TODO step over using debugger
		    System.out.println("Step over button pressed");
		}
	    });
	}
    }

    private class StepOutButton extends JButton {
	private StepOutButton() {
	    try {
		setIcon(new ImageIcon(ImageIO.read(
			new File("images/stepOut.png")).getScaledInstance(
			FlowClient.BUTTON_ICON_SIZE,
			FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	    setBorder(FlowClient.EMPTY_BORDER);
	    addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
		    // TODO step out using debugger
		    System.out.println("Step out button pressed");
		}
	    });
	}
    }
}
