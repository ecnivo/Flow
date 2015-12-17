package gui;

import java.awt.CardLayout;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import editing.EditPane;
import settings.SettingsPane;

public class FlowClient extends JFrame {

	private EditPane clientPanel;
	private SettingsPane settings;

	public static void main(String[] args) {
		new FlowClient();
	}

	public FlowClient() {
		// loads things
		super("Flow");

		// Sets size and location
		this.setSize(1200, 500);
		this.setLocation(50, 50);

		// sets the icon in the task bark
		try {
			this.setIconImage(ImageIO.read(new File("icon.png")));
		} catch (IOException e) {
			JOptionPane.showConfirmDialog(this, "Window icon not found",
					"Missing Image", JOptionPane.DEFAULT_OPTION,
					JOptionPane.ERROR_MESSAGE);
		}

		this.getContentPane().setLayout(new CardLayout());

		clientPanel = new EditPane();
		this.getContentPane().add(clientPanel);

		settings = new SettingsPane();
		this.getContentPane().add(settings);

		this.setResizable(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
	}
}
