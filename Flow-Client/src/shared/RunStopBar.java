package shared;

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

public class RunStopBar extends JToolBar {
    public RunStopBar(GenericConsole console) {
	setBorder(FlowClient.EMPTY_BORDER);
	setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

	add(new RunButton(console));
	add(new StopButton(console));

	setFloatable(false);
	setRollover(true);
    }

    private class RunButton extends JButton {
	public RunButton(GenericConsole console) {
	    setToolTipText("Compiles, then runs the file currently open in the editor");
	    setBorder(FlowClient.EMPTY_BORDER);
	    try {
		setIcon(new ImageIcon(ImageIO.read(new File("images/run.png")).getScaledInstance(FlowClient.BUTTON_ICON_SIZE, FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
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

	public StopButton(GenericConsole console) {
	    setToolTipText("Stops the currently running program");
	    setBorder(FlowClient.EMPTY_BORDER);
	    try {
		setIcon(new ImageIcon(ImageIO.read(new File("images/stop.png")).getScaledInstance(FlowClient.BUTTON_ICON_SIZE, FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
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
