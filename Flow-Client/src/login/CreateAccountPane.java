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
import javax.swing.BoxLayout;
import java.awt.Component;
import javax.swing.Box;
import java.awt.Dimension;

public class CreateAccountPane extends JPanel {
    private AuthenticationPanelManager manager;
    private JLabel usernamePrompt;
    private UsernameBox usernameEntry;
    private JLabel passwordPrompt;
    private JPasswordField passwordEntry;
    private JButton newAccountButton;
    private Component verticalStrut;
    private Component verticalStrut_1;
    private Component verticalStrut_2;
    private Component verticalStrut_3;

    public CreateAccountPane(AuthenticationPanelManager manager) {
	this.manager = manager;
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	
	verticalStrut_3 = Box.createVerticalStrut(20);
	add(verticalStrut_3);

	JLabel title = new JLabel("New Account");
	title.setAlignmentX(Component.CENTER_ALIGNMENT);
	title.setFont(new Font("Tahoma", Font.BOLD, 14));
	add(title);
	
	verticalStrut = Box.createVerticalStrut(20);
	add(verticalStrut);

	usernamePrompt = new JLabel(
		"Username (you cannot change this!)");
	usernamePrompt.setAlignmentX(Component.CENTER_ALIGNMENT);
	add(usernamePrompt);

	usernameEntry = new UsernameBox();
	usernameEntry.setPreferredSize(new Dimension(128, 24));
	usernameEntry.setMaximumSize(new Dimension(128, 24));
	add(usernameEntry);
	
	verticalStrut_1 = Box.createVerticalStrut(20);
	add(verticalStrut_1);

	passwordPrompt = new JLabel("Password");
	passwordPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);
	add(passwordPrompt);

	passwordEntry = new JPasswordField();
	passwordEntry.setMaximumSize(new Dimension(128, 24));
	passwordEntry.setPreferredSize(new Dimension(128, 24));
	passwordEntry.setToolTipText("The password for Flow");
	add(passwordEntry);

	newAccountButton = new JButton("Sign Up!");
	newAccountButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	newAccountButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		// TODO send new user name and password to server to add them to
		// the list
		CreateAccountPane.this.manager.switchToEditor();
	    }
	});
	
	verticalStrut_2 = Box.createVerticalStrut(20);
	add(verticalStrut_2);
	add(newAccountButton);
    }
}
