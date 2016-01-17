package gui;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import message.Data;
import shared.Communicator;

@SuppressWarnings("serial")
public class FlowClient extends JFrame {

    public static final boolean NETWORK = true;
    public static final javax.swing.border.Border EMPTY_BORDER = BorderFactory.createEmptyBorder(0, 0, 0, 0);
    public static final int BUTTON_ICON_SIZE = 24;
    public static final int SCROLL_SPEED = 12;
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 10244;
    private PanelManager manager;

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
	Communicator.initAsync();

	manager = new PanelManager(this);
	this.add(manager);

	this.setResizable(true);
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

	    @Override
	    public void run() {
		Data logOff = new Data("end_session");
		UUID sessionID = Communicator.getSessionID();
		if (sessionID == null)
		    return;
		logOff.put("session_id", sessionID);
		Communicator.communicate(logOff);
	    }
	}));
	this.setSize((int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.8), (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.8));
	this.setLocation((int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.1), (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.1));
	this.setVisible(true);
    }

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, IOException {
	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	new FlowClient();
    }
}
