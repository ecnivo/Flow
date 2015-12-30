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

import struct.User;

public class UserCaret extends JComponent {

    private Point location;
    private Color userColor;
    private JLabel infoLabel;

    private static final int SHOW_INFO_DISTANCE = 25;

    public UserCaret(User user, EditArea editArea) {
	setLayout(null);
	location = new Point(0, 0);

	int red = (int) (Math.random() * 150) + 30;
	int green = (int) (Math.random() * 150) + 30;
	int blue = (int) (Math.random() * 150) + 30;

	switch ((int) Math.random() * 3) {
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

	infoLabel = new JLabel(user.getUsername());
	infoLabel.setBackground(userColor);
	infoLabel.setForeground(Color.WHITE);
	infoLabel.setFont(new Font("Tw Cen MT", Font.PLAIN, 10));
	infoLabel.setBounds(editArea.getInsets().left,
		(int) (editArea.getInsets().top + location.getY()),
		(int) infoLabel.getSize().getWidth(), (int) infoLabel.getSize()
			.getHeight());
	add(infoLabel);

	addMouseMotionListener(new MouseMotionListener() {
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

    @Override
    protected void paintComponent(Graphics g) {
	super.paintComponent(g);

	Graphics2D g2d = (Graphics2D) g;
	g2d.setColor(userColor);
	g2d.fillRect((int) (location.getX()), (int) location.getY(), 2, 17);
    }

    public void charMoved(int line, int charPos) {
	location = new Point(charPos * 8, line * 17);
	infoLabel.setVisible(true);
    }

    public Point getLocation() {
	return location;
    }
}