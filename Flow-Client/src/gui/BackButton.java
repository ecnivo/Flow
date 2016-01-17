
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
import editing.EditPane;

/**
 * A button that goes to a particular pane
 * 
 * @author Vince Ou
 *
 */
public class BackButton extends JButton {

	/**
	 * Creates a new BackButton
	 * 
	 * @param target
	 *        the place that the backbutton should navigate to
	 * @param manager
	 *        the PanelManager to call it on
	 */
	public BackButton(JPanel target, PanelManager manager) {
		// Sets the icon
		try {
			setIcon(new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/backButton.png")).getScaledInstance(FlowClient.BUTTON_ICON_SIZE, FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		addActionListener(new ActionListener() {

			/**
			 * Depending on the type of possible targets, it will tell the cardlayout in the
			 * PanelManager to switch to particular card.
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				if (target instanceof LoginPane) {
					manager.switchToLogin();
				} else if (target instanceof CreateAccountPane) {
					manager.switchToCreateAccount();
				} else if (target instanceof EditPane) {
					manager.switchToEditor();
				}
			}
		});
	}
}
