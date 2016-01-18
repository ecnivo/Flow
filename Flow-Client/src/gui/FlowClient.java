
package gui;

import java.awt.Toolkit;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;

import message.Data;
import shared.Communicator;

/**
 * The Main/Client program
 * 
 * @author Vince Ou
 *
 */
@SuppressWarnings("serial")
public class FlowClient extends JFrame {

	// Bunch of important stuff
	public static final Border	EMPTY_BORDER		= BorderFactory.createEmptyBorder(0, 0, 0, 0);
	public static final int		BUTTON_ICON_SIZE	= 24;
	public static final int		SCROLL_SPEED		= 12;
	private static final String	HOST				= "127.0.0.1";
	private static final int	PORT				= 10244;
	private PanelManager		manager;

	/**
	 * Creates a new FlowClient
	 * 
	 * @throws IOException
	 */
	public FlowClient() throws IOException {
		// loads things
		super("Flow");

		// sets the icon in the task bar
		try {
			this.setIconImage(ImageIO.read(ClassLoader.getSystemResource("images/icon.png")));
		} catch (IOException e) {
			JOptionPane.showConfirmDialog(this, "Window icon not found", "Missing Image", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
		}

		// Sets up communications with the server
		Communicator.initComms(HOST, PORT);

		// Creates a new PanelManager
		manager = new PanelManager(this);
		this.add(manager);

		// JFrame setup
		this.setResizable(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Logs off the client when the "big red X" is pressed
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				// Generates a new data packet and sends to server
				Data logOff = new Data("end_session");
				UUID sessionID = Communicator.getSessionID();
				if (sessionID == null)
					return;
				Communicator.killAsync();
				logOff.put("session_id", sessionID);
				Communicator.communicate(logOff);

			}
		}));
		this.setSize((int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.8), (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.8));
		this.setLocation((int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.1), (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.1));
		this.setVisible(true);
	}

	/**
	 * Starts
	 * 
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws UnsupportedLookAndFeelException
	 * @throws IOException
	 */
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, IOException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		new FlowClient();
	}

	public void resetUI() {
		this.remove(manager);
		manager = new PanelManager(this);
		this.add(manager);
		revalidate();
		repaint();
	}
}
