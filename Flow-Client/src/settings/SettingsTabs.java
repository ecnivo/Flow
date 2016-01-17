
package settings;

import gui.FlowClient;
import gui.PanelManager;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;

import message.Data;
import shared.Communicator;

/**
 * Some tabs to manage the many many settings we have
 * 
 * @author Vince
 *
 */
@SuppressWarnings("serial")
public class SettingsTabs extends JTabbedPane {

	private static final Dimension	TEXT_BOX_SIZE	= new Dimension(256, 24);

	/*
	 * Change user avatar
	 * 
	 * Change password
	 * 
	 * Log out
	 * 
	 * Theme (planned!)
	 * 
	 * Close account
	 * 
	 * Manage "friends" (planned)
	 */
	/**
	 * Creates a new SettingsTabs
	 * 
	 * @param panMan
	 *        the associated PanelManager
	 */
	public SettingsTabs(PanelManager panMan) {
		// Sets up the tab
		setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		setBorder(FlowClient.EMPTY_BORDER);

		// Creates the "change avatar" tab
		SettingsTab avatar = new SettingsTab("Avatar");
		avatar.add(new JLabel("Personalize your avatar"));
		JButton selectAvatar = new JButton("Select...");
		selectAvatar.addActionListener(new ActionListener() {

			/**
			 * Pops open a new file chooser for the user to pick their avatar
			 */
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Creates a new file chooser
				JFileChooser avatarChooser = new JFileChooser();
				avatarChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {

					@Override
					public String getDescription() {
						return "Only PNG, JPG, GIF, BMP formats";
					}

					@Override
					public boolean accept(File f) {
						String name = f.getName();
						return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".gif") || name.endsWith(".bmp");
					}
				});
				avatarChooser.setDialogTitle("Select Avatar Image");
				// Tries to get the image from the HDD
				if (avatarChooser.showOpenDialog(SettingsTabs.this) == JFileChooser.APPROVE_OPTION) {
					try {
						ImageIcon newAvatar = new ImageIcon(ImageIO.read(avatarChooser.getSelectedFile()).getScaledInstance(32, 32, Image.SCALE_SMOOTH));
					} catch (IOException e) {
						e.printStackTrace();
					}
					// TODO send image to server, show joptionpane when done
				}
			}
		});
		avatar.add(selectAvatar);

		// Creates the "change password" tab
		SettingsTab passChange = new SettingsTab("Password");
		passChange.add(new JLabel("Change your password"));
		// First entry field
		JPasswordField passField = new JPasswordField();
		passField.setMaximumSize(TEXT_BOX_SIZE);
		passField.setPreferredSize(TEXT_BOX_SIZE);
		passChange.add(passField);
		passChange.add(new JLabel("Re-type your password"));
		// Confirm entry field
		JPasswordField retypePass = new JPasswordField();
		retypePass.setMaximumSize(TEXT_BOX_SIZE);
		retypePass.setPreferredSize(TEXT_BOX_SIZE);
		passChange.add(retypePass);
		// Button to save
		JButton savePassword = new JButton("Save new password");
		savePassword.addActionListener(new ActionListener() {

			/**
			 * Sends to server to approve
			 */
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Checks the value inside the boxes to make sure they're valid and match
				if (String.copyValueOf(passField.getPassword()).length() < 1) {
					JOptionPane.showConfirmDialog(null, "Please enter a password.", "Invalid password", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (!Arrays.equals(passField.getPassword(), retypePass.getPassword())) {
					JOptionPane.showConfirmDialog(null, "The two passwords do not match.\nPlease try again.\n" + "No changes have been made to your account", "Passwords don't match!", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
					return;
				}
				// Creates a new message to send to the server
				Data newPass = new Data("user");
				newPass.put("user_type", "CHANGE_PASSWORD");
				newPass.put("session_id", Communicator.getSessionID());
				newPass.put("password", String.copyValueOf(passField.getPassword()));
				String status = Communicator.communicate(newPass).get("status", String.class);

				if (status.equals("PASSWORD_INVALID")) {
					JOptionPane.showConfirmDialog(null, "The entered password is invalid. Typically, this is because\n" + "of the presence of special characters", "Invalid password", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
					return;
				} else if (status.equals("OK"))
					JOptionPane.showConfirmDialog(null, "Your Flow password has successfully been changed", "Password change success", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
			}
		});
		passChange.add(savePassword);

		// Adds the "logout" tab
		SettingsTab logout = new SettingsTab("Logout");
		logout.add(new JLabel("Logout"));
		// Just a single "logout" button
		JButton logoutButton = new JButton("Logout/Switch user");
		logoutButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Creates an "end session" message to send to the server
				Data endSession = new Data("end_session");
				System.out.println(Communicator.getSessionID());
				endSession.put("session_id", Communicator.getSessionID());
				Communicator.communicate(endSession);
				Communicator.setSessionID(null);
				// Resets everything and everything
				panMan.switchToLogin();
				panMan.resetUI();
				return;
			}
		});
		logout.add(logoutButton);

		// Delete Flow account tab
		SettingsTab closeAccount = new SettingsTab("Close account");
		closeAccount.add(new JLabel("Close account"));
		// Again, just a single button
		JButton confirmButton = new JButton("Confirm close account");
		confirmButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Shows a daunting message box
				String response = JOptionPane.showInputDialog(null, "WARNING: Deleting your account means that all of the projects that you created will be lost," + "\nand all projects that you have been invited to collaborate will lose you as a collaborator."
						+ "\nBefore closing your account, please make sure to back up all data using the export function." + "\nIf you are sure that you want to close your account, type \"close my account\" into the text box below." + "\n\nTHIS IS YOUR LAST CHANCE TO CHANGE YOUR MIND.", "Confirm account deletion", JOptionPane.WARNING_MESSAGE);
				if (response != null && response.equals("close my account")) {
					// Prepares a new message to the server
					Data closeAccountRequest = new Data("user");
					closeAccountRequest.put("user_type", "CLOSE_ACCOUNT");
					closeAccountRequest.put("session_id", Communicator.getSessionID());
					if (Communicator.communicate(closeAccountRequest).get("status", String.class).startsWith("OK")) {
						// Send the message, with confirmation box
						JOptionPane.showConfirmDialog(null, "Your Flow account has been successfully deleted.", "Account deletion success", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
						// Clears things, and switches back to the login window
						Communicator.setSessionID(null);
						panMan.switchToLogin();
						panMan.resetUI();
						return;
					} else {
						// Error catching
						JOptionPane.showConfirmDialog(null, "Your Flow account could not be closed for some reason.\nTry logging out, restarting Flow,\nand if the problem persists, please submit a bug report on Github.", "Account deletion failed.", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
					}
				} else {
					// User chickens out
					JOptionPane.showConfirmDialog(null, "Your Flow account has not been deleted." + "\nNo modifications were made.", "Account deletion fail", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		closeAccount.add(confirmButton);
	}

	/**
	 * Custom Tab for the JTabbedPane
	 * @author Vince Ou
	 *
	 */
	private class SettingsTab extends JPanel {

		private JScrollPane				scrolling;
		private ArrayList<Component>	children;
		private SpringLayout			layout;
		private final static int		SEP_GAP	= 25;

		/**
		 * Creates a new SettingsTab
		 * @param name the name of the tab header
		 */
		private SettingsTab(String name) {
			// Just a bunch of settings
			children = new ArrayList<Component>();
			setBorder(FlowClient.EMPTY_BORDER);
			layout = new SpringLayout();
			setLayout(layout);
			scrolling = new JScrollPane(this);
			scrolling.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			SettingsTabs.this.addTab(name, scrolling);
		}

		@Override
		public Component add(Component component) {
			// Does swing layout stuff
			layout.putConstraint(SpringLayout.WEST, component, SEP_GAP, SpringLayout.WEST, SettingsTab.this);

			if (children.size() == 0) {
				layout.putConstraint(SpringLayout.NORTH, component, SEP_GAP, SpringLayout.NORTH, SettingsTab.this);
			} else {
				layout.putConstraint(SpringLayout.NORTH, component, SEP_GAP, SpringLayout.NORTH, children.get(children.size() - 1));
			}
			super.add(component);
			children.add(component);
			return component;
		}
	}
}
