package login;

import gui.PanelManager;

import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;

public class LoginPane extends JPanel {
    private PanelManager panMan;

    public LoginPane(PanelManager panMan) {
	setBackground(Color.WHITE);
	this.panMan = panMan;
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

	java.awt.Component verticalStrut_4 = Box.createVerticalStrut(20);
	add(verticalStrut_4);
	// TODO add background picture

	// TODO make title less gross (logo, possibly?)
	JLabel title = new JLabel();
	title.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
	add(title);
	try {
	    title.setIcon(new ImageIcon(ImageIO.read(
		    new File("images/flow.png")).getScaledInstance(414, 128,
		    Image.SCALE_SMOOTH)));
	} catch (IOException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}

	java.awt.Component verticalStrut_3 = Box.createVerticalStrut(20);
	add(verticalStrut_3);

	JLabel usernamePrompt = new JLabel("Username");
	add(usernamePrompt);
	usernamePrompt.setSize(128, 28);
	usernamePrompt.setAlignmentX(CENTER_ALIGNMENT);

	UsernameBox usernameEntry = new UsernameBox();
	usernameEntry.setMaximumSize(new Dimension(128, 24));
	add(usernameEntry);

	java.awt.Component verticalStrut = Box.createVerticalStrut(20);
	add(verticalStrut);

	JLabel passwordPrompt = new JLabel("Password");
	add(passwordPrompt);
	passwordPrompt.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

	JPasswordField passwordEntry = new JPasswordField();
	passwordEntry.setMaximumSize(new Dimension(128, 24));
	add(passwordEntry);
	passwordEntry.setToolTipText("Your Flow password");

	java.awt.Component verticalStrut_1 = Box.createVerticalStrut(20);
	add(verticalStrut_1);

	JButton logInButton = new JButton("Login");
	add(logInButton);
	logInButton.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

	java.awt.Component verticalStrut_2 = Box.createVerticalStrut(20);
	add(verticalStrut_2);

	JButton newAccountButton = new JButton(
		"<html>No Account?<br>Create one!</html>");
	newAccountButton.setPreferredSize(new Dimension(128, 32));
	newAccountButton.setMinimumSize(new Dimension(32, 2));
	newAccountButton.setMaximumSize(new Dimension(128, 32));
	add(newAccountButton);
	newAccountButton.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
	newAccountButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		LoginPane.this.panMan.switchToCreateAccount();
	    }
	});
	logInButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		// TODO add something here to send username and password off for
		// authentication
		LoginPane.this.panMan.switchToEditor();
	    }
	});
    }
}
