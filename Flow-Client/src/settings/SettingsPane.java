package settings;

import gui.CustomScrollPane;
import gui.FlowClient;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class SettingsPane extends JPanel {
    private JPasswordField passwordField;
    private JPasswordField retypePasswordField;
    private JTextField closeAccountConfirm;

    public SettingsPane() {
	setAlignmentY(Component.TOP_ALIGNMENT);
	setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

	Component horizontalStrut = Box.createHorizontalStrut(20);
	add(horizontalStrut);

	JPanel panel = new JPanel();
	panel.setAlignmentY(Component.TOP_ALIGNMENT);
	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

	CustomScrollPane scrolling = new CustomScrollPane(panel);
	scrolling.setBorder(FlowClient.emptyBorder());

	JLabel title = new JLabel("Settings");
	title.setFont(new Font("Tahoma", Font.BOLD, 18));
	panel.add(title);

	Component verticalStrut = Box.createVerticalStrut(20);
	panel.add(verticalStrut);

	JLabel avatarChangePrompt = new JLabel("Change avatar");
	panel.add(avatarChangePrompt);

	JButton selectAvatar = new JButton("Select...");
	selectAvatar.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		JFileChooser avatarChooser = new JFileChooser();
		avatarChooser
			.setFileFilter(new javax.swing.filechooser.FileFilter() {

			    @Override
			    public String getDescription() {
				return "Only PNG, JPG, GIF (first frame), BMP formats";
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
	panel.add(selectAvatar);

	Component verticalStrut_1 = Box.createVerticalStrut(20);
	panel.add(verticalStrut_1);

	JLabel passwordChangePrompt = new JLabel("Change password");
	panel.add(passwordChangePrompt);

	passwordField = new JPasswordField();
	passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
	passwordField.setMaximumSize(new Dimension(128, 24));
	panel.add(passwordField);

	JLabel retypePassword = new JLabel("Re-type password");
	panel.add(retypePassword);

	retypePasswordField = new JPasswordField();
	retypePasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);
	retypePasswordField.setMaximumSize(new Dimension(128, 24));
	panel.add(retypePasswordField);

	JButton savePassword = new JButton("Save new password");
	savePassword.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		// TODO hash password and send off to server. Show joptionpane
		// when done.
	    }
	});
	panel.add(savePassword);

	Component verticalStrut_2 = Box.createVerticalStrut(20);
	panel.add(verticalStrut_2);

	JButton logOutButton = new JButton("Log out");
	logOutButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		// Close session, and go to login screen
	    }
	});
	panel.add(logOutButton);

	Component verticalStrut_3 = Box.createVerticalStrut(20);
	panel.add(verticalStrut_3);

	JLabel deleteAccountButton = new JLabel("Close account");
	panel.add(deleteAccountButton);

	JTextArea closeAccountWarning = new JTextArea();
	closeAccountWarning.setForeground(Color.RED);
	closeAccountWarning.setMaximumSize(new Dimension(320, 512));
	closeAccountWarning.setAlignmentX(Component.LEFT_ALIGNMENT);
	closeAccountWarning.setFont(new Font("Tahoma", Font.PLAIN, 11));
	closeAccountWarning
		.setText("WARNING: Closing your account means that all your projects will be deleted, and all contributors to these projects will lose access to the code. Please back up all necessary information before closing your account.\r\nType \"close my account\" to confirm.");
	closeAccountWarning.setEnabled(false);
	closeAccountWarning.setEditable(false);
	closeAccountWarning.setWrapStyleWord(true);
	closeAccountWarning.setLineWrap(true);
	closeAccountWarning.setOpaque(false);
	panel.add(closeAccountWarning);

	closeAccountConfirm = new JTextField();
	closeAccountConfirm.setMaximumSize(new Dimension(128, 24));
	panel.add(closeAccountConfirm);
	closeAccountConfirm.setColumns(10);

	JButton btnConfirm = new JButton("Confirm");
	btnConfirm.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (closeAccountConfirm.getText().equals("close my account"))
		    // TODO send message to server, close account, show
		    // joptionpane go to login screen
		    System.out.println("Temp message to avoid error");
	    }
	});
	panel.add(btnConfirm);

	add(scrolling);
    }
}