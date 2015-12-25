package settings;

import gui.FlowClient;
import gui.PanelManager;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
	setBorder(FlowClient.EMPTY_BORDER);

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
	passField.setPreferredSize(TEXT_BOX_SIZE);
//	passField.addKeyListener(new PassFieldListener(passField));
	passChange.add(passField);
	passChange.add(new JLabel("Re-type your password"));
	JPasswordField retypePass = new JPasswordField();
	retypePass.setMaximumSize(TEXT_BOX_SIZE);
	retypePass.setPreferredSize(TEXT_BOX_SIZE);
//	retypePass.addKeyListener(new PassFieldListener(retypePass));
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
	JButton confirmButton = new JButton("Confirm close account");
	confirmButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		String response = JOptionPane
			.showInputDialog(
				null,
				"WARNING: Deleting your account means that all of the projects that you created will be lost,"
					+ "\nand all projects that you have been invited to collaborate will lose you as a collaborator."
					+ "\nBefore closing your account, please make sure to back up all data using the export function."
					+ "\nIf you are sure that you want to close your account, type \"close my account\" into the text box below."
					+ "\n\nTHIS IS YOUR LAST CHANCE TO CHANGE YOUR MIND.",
				"Confirm account deletion",
				JOptionPane.WARNING_MESSAGE);
		if (response != null && response.equals("close my account")) {
		    // TODO user has confirmed to close their account. Log out,
		    // send close account stuff to server and delete everything
		    // that's theirs. Remove them from all collaborators.
		    JOptionPane.showConfirmDialog(null,
			    "Your Flow account has been successfully deleted.",
			    "Account deletion success",
			    JOptionPane.DEFAULT_OPTION,
			    JOptionPane.INFORMATION_MESSAGE);
		} else {
		    JOptionPane
			    .showConfirmDialog(
				    null,
				    "Your Flow account has not been deleted."
				    + "\nNo modifications were made.",
				    "Account deletion fail",
				    JOptionPane.DEFAULT_OPTION,
				    JOptionPane.INFORMATION_MESSAGE);
		}
	    }
	});
	closeAccount.add(confirmButton);
    }

    private class SettingsTab extends JPanel {
	private JScrollPane scrolling;
	private ArrayList<Component> children;
	private SpringLayout layout;
	private final static int SEP_GAP = 25;

	private SettingsTab(String name) {
	    children = new ArrayList<Component>();
	    setBorder(FlowClient.EMPTY_BORDER);
	    layout = new SpringLayout();
	    setLayout(layout);
	    scrolling = new JScrollPane(this);
	    scrolling
		    .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	    SettingsPane.this.addTab(name, scrolling);
	}

	@Override
	public Component add(Component component) {
	    layout.putConstraint(SpringLayout.WEST, component, SEP_GAP,
		    SpringLayout.WEST, SettingsTab.this);

	    if (children.size() == 0) {
		layout.putConstraint(SpringLayout.NORTH, component, SEP_GAP,
			SpringLayout.NORTH, SettingsTab.this);
	    } else {
		layout.putConstraint(SpringLayout.NORTH, component, SEP_GAP,
			SpringLayout.NORTH, children.get(children.size() - 1));
	    }
	    super.add(component);
	    children.add(component);
	    return component;
	}
    }

//    private class PassFieldListener implements KeyListener {
//
//	private JPasswordField field;
//
//	private PassFieldListener(JPasswordField field) {
//	    this.field = field;
//	}
//
//	@Override
//	public void keyPressed(KeyEvent e) {
//	    // nothing
//	}
//
//	@Override
//	public void keyReleased(KeyEvent e) {
//	    // nothing
//	}
//
//	@Override
//	public void keyTyped(KeyEvent e) {
//	    if (e.getKeyChar() == KeyEvent.VK_DELETE
//		    || e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
//		field.setText("");
//	    }
//	}
//
//    }
}