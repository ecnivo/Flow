package gui;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import shared.Communicator;

@SuppressWarnings("serial")
public class FlowClient extends JFrame {

    public static final boolean NETWORK = true;

    private static final String HOST = "10.242.179.9";

    private static final int PORT = 10244;

    private PanelManager manager;
    public static final javax.swing.border.Border EMPTY_BORDER = BorderFactory.createEmptyBorder(0, 0, 0, 0);
    public static final int BUTTON_ICON_SIZE = 24;
    public static final int SCROLL_SPEED = 12;

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, IOException {
	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	new FlowClient();
    }

    public FlowClient() throws IOException {
	// loads things
	super("Flow");

	// sets the icon in the task bar
	try {
	    this.setIconImage(ImageIO.read(new File("images/icon.png")));
	} catch (IOException e) {
	    JOptionPane.showConfirmDialog(this, "Window icon not found", "Missing Image", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
	}

	Communicator.initComms(HOST, PORT);

	manager = new PanelManager(this);
	this.add(manager);

	this.setResizable(true);
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	this.setSize((int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.8), (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.8));
	this.setLocation((int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.1), (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.1));
	this.setVisible(true);
    }
}
