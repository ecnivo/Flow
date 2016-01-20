
package login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * A box for the user to input their username
 * @author Vince
 *
 */
class UsernameBox extends JTextField {

	private static final String	DEFAULT_TEXT	= "Username";
	private static final Color	DESELECT		= Color.GRAY;

	/**
	 * Creates a new UsernameBox
	 */
	public UsernameBox() {
		// Swing setup
		setPreferredSize(new Dimension(128, 32));
		this.setToolTipText("Your Flow username");
		this.setText(DEFAULT_TEXT);
		this.setForeground(DESELECT);
		this.addFocusListener(new FocusListener() {

			/**
			 * Will set the text to default when lost focus
			 */
			@Override
			public void focusLost(FocusEvent e) {
				if (getText().trim().equals("")) {
					setForeground(DESELECT);
					setText(DEFAULT_TEXT);
				}
			}

			/**
			 * Clears when focus gained again
			 */
			@Override
			public void focusGained(FocusEvent e) {
				setForeground(Color.BLACK);
				if (getText().equals("Username")) {
					setText("");
				}
			}
		});
		this.setColumns(10);
	}
}
