package gui;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import login.CreateAccountPane;
import login.LoginPane;

public class BackButton extends JButton {
    public BackButton(JPanel target, PanelManager manager) {
	try {
	    setIcon(new ImageIcon(ImageIO.read(
		    new File("images/backButton.png")).getScaledInstance(32,
		    32, Image.SCALE_SMOOTH)));
	} catch (IOException e) {
	    e.printStackTrace();
	}

	addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		if (target instanceof LoginPane) {
		    manager.switchToLogin();
		} else if (target instanceof CreateAccountPane) {
		    manager.switchToCreateAccount();
		}
	    }
	});
    }
}
