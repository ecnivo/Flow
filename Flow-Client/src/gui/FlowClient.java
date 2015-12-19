package gui;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class FlowClient extends JFrame {

    private PanelManager manager;

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

	manager = new PanelManager();
	this.add(manager);

	this.setResizable(true);
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	this.setSize((int) (Toolkit.getDefaultToolkit().getScreenSize()
		.getWidth() * 0.8), (int) (Toolkit.getDefaultToolkit()
		.getScreenSize().getHeight() * 0.8));
	this.setLocation((int) (Toolkit.getDefaultToolkit().getScreenSize()
		.getWidth() * 0.1), (int) (Toolkit.getDefaultToolkit()
		.getScreenSize().getHeight() * 0.1));
	this.pack();
	this.setVisible(true);
    }
}
