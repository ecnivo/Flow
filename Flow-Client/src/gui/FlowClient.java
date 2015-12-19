package gui;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class FlowClient extends JFrame {

    public static void main(String[] args) throws ClassNotFoundException,
	    InstantiationException, IllegalAccessException,
	    UnsupportedLookAndFeelException {
	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	new FlowClient();
    }

    public FlowClient() {
	// loads things
	super("Flow");

	// sets the icon in the task bark
	try {
	    this.setIconImage(ImageIO.read(new File("icon.png")));
	} catch (IOException e) {
	    JOptionPane.showConfirmDialog(this, "Window icon not found",
		    "Missing Image", JOptionPane.DEFAULT_OPTION,
		    JOptionPane.ERROR_MESSAGE);
	}

	// Sets size and location
	this.setSize(1200, 500);
	this.setLocation(50, 50);

	this.add(new PanelManager());

	this.setResizable(true);
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	this.pack();
	this.setVisible(true);
    }
}
