
package editing;

import shared.EditArea;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * Used to represent a remote user's cursor location on the client's screen
 * 
 * @author Vince Ou
 *
 */
@SuppressWarnings("serial")
public class UserCaret extends JComponent {

	private Point				location;
	private final Color userColor;
	private final JLabel infoLabel;
	private final EditArea editArea;
	private final String name;

	private static final int	SHOW_INFO_DISTANCE	= 25;

	/**
	 * Creates a new UserCaret
	 * 
	 * @param user
	 *        the user's name
	 * @param editArea
	 *        the EditArea it is associated with
	 */
	public UserCaret(String user, EditArea editArea) {
		// TODO whenever usercarets are created, they need to be initialized
		// with a listener for the caret movements of other users
		this.editArea = editArea;
		name = user;
		setLayout(null);
		location = new Point(0, 0);

		// Sets a colour
		int red = (int) (Math.random() * 150) + 35;
		int green = (int) (Math.random() * 150) + 35;
		int blue = (int) (Math.random() * 150) + 35;

		// Makes the colour differential greater
		switch ((int) (Math.random() * 3)) {
			case 0:
				red -= 20;
				break;

			case 1:
				green -= 20;
				break;

			case 2:
				blue -= 20;
				break;
		}
		userColor = new Color(red, green, blue, 225);

		// Creates a new label that will be shown when the mouse is close enough
		infoLabel = new JLabel(user);
		infoLabel.setBackground(userColor);
		infoLabel.setForeground(Color.WHITE);
		infoLabel.setFont(new Font("Tw Cen MT", Font.PLAIN, 10));
		infoLabel.setBounds(editArea.getInsets().left, (int) (editArea.getInsets().top + location.getY()), (int) infoLabel.getSize().getWidth(), (int) infoLabel.getSize().getHeight());
		add(infoLabel);

		addMouseMotionListener(new MouseMotionListener() {

			/**
			 * Only shows the label when needed
			 */
			@Override
			public void mouseMoved(MouseEvent e) {
				if (e.getPoint().distance(location) < SHOW_INFO_DISTANCE)
					infoLabel.setVisible(true);
				else
					infoLabel.setVisible(false);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				// nothing
			}
		});
	}

	/**
	 * Sets a new location for the caret
	 *
	 */
	public void moveTo(Point location) {
		// TODO since we're not using lines, do something about line wrapping?
		this.location = location;
		// Changes its location
		infoLabel.setBounds(editArea.getInsets().left, (int) (editArea.getInsets().top + location.getY()), (int) infoLabel.getSize().getWidth(), (int) infoLabel.getSize().getHeight());
		repaint();
	}

	/**
	 * Gets the location of the cursor
	 * 
	 * @return the location of the cursor
	 */
	public Point getLocation() {
		return location;
	}

	public String toString() {
		return name;
	}

	/**
	 * Gets the colour of the cursor
	 * @return the colour
	 */
	public Color getColor() {
		return userColor;
	}
}
