
package editing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JLabel;

import shared.EditArea;

/**
 * Used to represent a remote user's cursor location on the client's screen
 * 
 * @author Vince Ou
 *
 */
@SuppressWarnings("serial")
public class UserCaret extends JComponent {

	private Point				location;
	private Color				userColor;
	private JLabel				infoLabel;
	private EditArea			editArea;

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
		setLayout(null);
		location = new Point(0, 0);

		// Sets a colour
		int red = (int) (Math.random() * 150) + 30;
		int green = (int) (Math.random() * 150) + 30;
		int blue = (int) (Math.random() * 150) + 30;

		// Makes the colour differential greater
		switch ((int) (Math.random() * 3)) {
			case 0:
				red -= 15;
				break;

			case 1:
				green -= 15;
				break;

			case 2:
				blue -= 15;
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
	 * Paints the cursor
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(userColor);
		g2d.fillRect((int) (location.getX()), (int) location.getY(), 2, 17);
	}

	/**
	 * Sets a new location for the caret
	 * 
	 * @param line
	 *        the n'th line
	 * @param charPos
	 *        the n'th character in said line
	 */
	public void caretMoved(int line, int charPos) {
		// TODO since we're not using lines, do something about line wrapping?
		location = new Point(charPos * 8, line * 17);
		// Changes its location
		infoLabel.setBounds(editArea.getInsets().left, (int) (editArea.getInsets().top + location.getY()), (int) infoLabel.getSize().getWidth(), (int) infoLabel.getSize().getHeight());
	}

	/**
	 * Gets the location of the cursor
	 * 
	 * @return the location of the cursor
	 */
	public Point getLocation() {
		return location;
	}
}
