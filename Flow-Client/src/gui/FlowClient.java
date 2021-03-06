
package gui;

import message.Data;
import shared.Communicator;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.IOException;
import java.util.UUID;

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
	public static boolean		HIDE				= true;
	public static final int		SCROLL_SPEED		= 12;
	public static final int PORT = 10244;
	private PanelManager		manager;

	/**
	 * Creates a new FlowClient
	 * 
	 * @throws IOException
	 */
	private FlowClient() {
		// loads things
		super("Flow");

		// sets the icon in the task bar
		try {
			this.setIconImage(ImageIO.read(ClassLoader.getSystemResource("images/icon.png")));
		} catch (IOException e) {
			JOptionPane.showConfirmDialog(this, "Window icon not found", "Missing Image", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
		}

		// Sets up communications with the server
		Communicator.initComms(JOptionPane.showInputDialog(null, "TEMP: ENTER IP", "127.0.0.1"));

		// Creates a new PanelManager
		manager = PanelManager.createNewInstance(this);
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

	public void resetUI() {
		this.remove(manager);
		manager = PanelManager.createNewInstance(this);
		this.add(manager);
		revalidate();
		repaint();
	}
}
