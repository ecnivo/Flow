package settings;

import gui.PanelManager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class SettingsPane extends JTabbedPane {

    private static final Dimension TEXT_BOX_SIZE = new Dimension(256, 24);

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
    public SettingsPane(PanelManager manager) {
	setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

	SettingsTab avatar = new SettingsTab("Avatar");
	avatar.add(new JLabel("Personalize your avatar"));
	JButton selectAvatar = new JButton("Select...");
	selectAvatar.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		JFileChooser avatarChooser = new JFileChooser();
		avatarChooser
			.setFileFilter(new javax.swing.filechooser.FileFilter() {

			    @Override
			    public String getDescription() {
				return "Only PNG, JPG, GIF, BMP formats";
			    }

			    @Override
			    public boolean accept(File f) {
				String name = f.getName();
				if (name.endsWith(".png")
					|| name.endsWith(".jpg")
					|| name.endsWith(".gif")
					|| name.endsWith(".bmp"))
				    return true;
				return false;
			    }
			});
		avatarChooser.setDialogTitle("Select Avatar Image");
		if (avatarChooser.showOpenDialog(SettingsPane.this) == JFileChooser.APPROVE_OPTION) {
		    try {
			ImageIcon newAvatar = new ImageIcon(ImageIO.read(
				avatarChooser.getSelectedFile())
				.getScaledInstance(32, 32, Image.SCALE_SMOOTH));
		    } catch (IOException e) {
			e.printStackTrace();
		    }
		    // TODO send image to server, show joptionpane when done
		}
	    }
	});
	avatar.add(selectAvatar);

	SettingsTab passChange = new SettingsTab("Password");
	passChange.add(new JLabel("Change your password"));
	JPasswordField passField = new JPasswordField();
	passField.setMaximumSize(TEXT_BOX_SIZE);
	passChange.add(passField);
	passChange.add(new JLabel("Re-type your password"));
	JPasswordField retypePass = new JPasswordField();
	retypePass.setMaximumSize(TEXT_BOX_SIZE);
	passChange.add(retypePass);
	JButton savePassword = new JButton("Save new password");
	savePassword.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		// TODO hash password and send off to server. Show joptionpane
		// when done.
	    }
	});
	passChange.add(savePassword);

	SettingsTab logout = new SettingsTab("Logout");
	logout.add(new JLabel("Logout"));
	JButton logoutButton = new JButton("Logout");
	logoutButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		// TODO log out the user, show login screen
		return;
	    }
	});
	logout.add(logoutButton);

	SettingsTab closeAccount = new SettingsTab("Close account");
	closeAccount.add(new JLabel("Close account"));
	JTextArea warning = new JTextArea(
		"WARNING: Closing your account means that all your projects will be deleted, and all contributors to these projects will lose access to the code."
			+ " Please back up all necessary information before closing your account.\r\nType \"close my account\" into the following text box to confirm.");
	warning.setFont(new Font("Tahoma", Font.PLAIN, 11));
	warning.setEditable(false);
	warning.setForeground(Color.RED);
	warning.setWrapStyleWord(true);
	warning.setLineWrap(true);
	warning.setOpaque(false);
	closeAccount.add(warning);
	JTextField confirm = new JTextField();
	confirm.setMaximumSize(TEXT_BOX_SIZE);
	closeAccount.add(confirm);
	confirm.setColumns(20);
	JButton confirmButton = new JButton("Confirm close account");
	confirmButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (confirmButton.getText().equals("close my account"))
		    // TODO send message to server, close account, show
		    // joptionpane go to login screen
		    System.out.println("Account closed button pressed");
		else
		    JOptionPane
			    .showConfirmDialog(
				    null,
				    "You did not type the confirmation message correctly\nIf you would like to close your account, type \"close my account\" without the quotation marks into the text box.",
				    "Incorrect Confirmation Text",
				    JOptionPane.DEFAULT_OPTION,
				    JOptionPane.WARNING_MESSAGE);
	    }
	});
	closeAccount.add(confirmButton);
    }

    private class SettingsTab extends JPanel {
	private JScrollPane scrolling;

	private SettingsTab(String name) {
	    scrolling = new JScrollPane(this);
	    scrolling
		    .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	    SettingsPane.this.addTab(name, this);
	}
    }
}