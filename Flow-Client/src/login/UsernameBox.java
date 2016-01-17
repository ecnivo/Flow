
package login;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

/**
 * A box for the user to input their username
 * @author Vince
 *
 */
public class UsernameBox extends JTextField {

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
