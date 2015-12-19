package login;

import gui.BackButton;
import gui.PanelManager;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;

public class CreateAccountPane extends JPanel {
    private PanelManager manager;
    private JPasswordField passwordField;

    public CreateAccountPane(PanelManager manager) {
	setBackground(Color.WHITE);
	this.manager = manager;
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

	Component verticalStrut_3 = Box.createVerticalStrut(20);
	add(verticalStrut_3);

	JLabel title = new JLabel("New Account");
	title.setAlignmentX(Component.CENTER_ALIGNMENT);
	title.setFont(new Font("Tahoma", Font.BOLD, 14));
	add(title);

	Component verticalStrut = Box.createVerticalStrut(20);
	add(verticalStrut);

	JLabel usernamePrompt = new JLabel("Username (you cannot change this!)");
	usernamePrompt.setAlignmentX(Component.CENTER_ALIGNMENT);
	add(usernamePrompt);

	JComponent usernameEntry = new UsernameBox();
	usernameEntry.setPreferredSize(new Dimension(128, 24));
	usernameEntry.setMaximumSize(new Dimension(128, 24));
	add(usernameEntry);

	Component verticalStrut_1 = Box.createVerticalStrut(20);
	add(verticalStrut_1);
	
		JLabel passwordPrompt = new JLabel("Password");
		passwordPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(passwordPrompt);
		
			JComponent passwordEntry = new JPasswordField();
			passwordEntry.setMaximumSize(new Dimension(128, 24));
			passwordEntry.setPreferredSize(new Dimension(128, 24));
			passwordEntry.setToolTipText("The password for Flow");
			add(passwordEntry);
	
		Component verticalStrut_4 = Box.createVerticalStrut(20);
		add(verticalStrut_4);
	
	JLabel label = new JLabel("Re-type password");
	label.setAlignmentX(0.5f);
	add(label);
	
	passwordField = new JPasswordField();
	passwordField.setToolTipText("The password for Flow");
	passwordField.setPreferredSize(new Dimension(128, 24));
	passwordField.setMaximumSize(new Dimension(128, 24));
	add(passwordField);
	
	Component verticalStrut_2 = Box.createVerticalStrut(20);
	add(verticalStrut_2);

	JPanel bottomButtons = new JPanel();
	bottomButtons.setBackground(Color.WHITE);
	add(bottomButtons);
	bottomButtons.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

	BackButton backButton = new BackButton(manager.getLoginPane(), manager);
	bottomButtons.add(backButton);
	backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	backButton.setHorizontalAlignment(SwingConstants.LEFT);

	JButton newAccountButton = new JButton("Sign Up!");
	bottomButtons.add(newAccountButton);
	newAccountButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	newAccountButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		// TODO send new user name and password to server to add them to
		// the list
		CreateAccountPane.this.manager.switchToEditor();
		//TODO show joptionpane when successful
	    }
	});
    }
}
