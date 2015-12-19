package login;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

public class CreateAccountPane extends JPanel {
    private AuthenticationPanelManager manager;
    private JLabel usernamePrompt;
    private UsernameBox usernameEntry;
    private JLabel passwordPrompt;
    private JPasswordField passwordEntry;
    private JButton newAccountButton;
    private static final int HEIGHT = 26;
    private static final int WIDTH = 128;
    private int xPos = (int) (CreateAccountPane.this.getWidth() / 2 - WIDTH / 2);

    public CreateAccountPane(AuthenticationPanelManager manager) {
	this.manager = manager;
	setLayout(null);

	JLabel title = new JLabel("New Account");
	title.setFont(new Font("Tahoma", Font.BOLD, 14));
	title.setBounds(xPos, 40, WIDTH, HEIGHT);
	add(title);

	usernamePrompt = new JLabel(
		"<html>Username<br>(you cannot change this!)</html>");
	usernamePrompt.setBounds(xPos, 78, WIDTH, HEIGHT);
	add(usernamePrompt);

	usernameEntry = new UsernameBox();
	usernameEntry.setBounds(xPos, 113, WIDTH, HEIGHT);
	add(usernameEntry);

	passwordPrompt = new JLabel("Password");
	passwordPrompt.setBounds(xPos, 162, WIDTH, HEIGHT);
	add(passwordPrompt);

	passwordEntry = new JPasswordField();
	passwordEntry.setToolTipText("The password for Flow");
	passwordEntry.setBounds(xPos, 187, WIDTH, HEIGHT);
	add(passwordEntry);

	newAccountButton = new JButton("Sign Up!");
	newAccountButton.setBounds(xPos, 240, 86, 23);
	newAccountButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		// TODO send new user name and password to server to add them to
		// the list
		CreateAccountPane.this.manager.switchToEditor();
	    }
	});
	add(newAccountButton);

	addComponentListener(new ComponentListener() {

	    @Override
	    public void componentShown(ComponentEvent e) {
		// nothing
	    }

	    @Override
	    public void componentResized(ComponentEvent e) {
		xPos = (int) (CreateAccountPane.this.getWidth() / 2 - WIDTH / 2);
		usernamePrompt.setLocation(xPos, 78);
		usernameEntry.setLocation(xPos, 162);
		passwordPrompt.setLocation(xPos, 187);
		newAccountButton.setLocation(xPos, 240);
		repaint();
	    }

	    @Override
	    public void componentMoved(ComponentEvent e) {
		// nothing
	    }

	    @Override
	    public void componentHidden(ComponentEvent e) {
		// nothing
	    }
	});
    }
}
