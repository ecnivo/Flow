package login;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTextField;

public class UsernameBox extends JTextField {

    private static final String DEFAULT_TEXT = "Username";

    public UsernameBox() {
	this.setToolTipText("Your Flow username");
	this.setText(DEFAULT_TEXT);
	this.setForeground(Color.GRAY);
	this.addMouseListener(new MouseListener() {

	    @Override
	    public void mouseReleased(MouseEvent e) {
		// nothing
	    }

	    @Override
	    public void mousePressed(MouseEvent e) {
		// nothing
	    }

	    @Override
	    public void mouseExited(MouseEvent e) {
		// nothing
	    }

	    @Override
	    public void mouseEntered(MouseEvent e) {
		// nothing
	    }

	    @Override
	    public void mouseClicked(MouseEvent e) {
		if (!UsernameBox.this.contains(e.getX(), e.getY())
			&& (UsernameBox.this.getText().equals(DEFAULT_TEXT) || UsernameBox.this
				.getText().equals(""))) {
		    UsernameBox.this.setForeground(Color.GRAY);
		    UsernameBox.this.setText(DEFAULT_TEXT);
		} else {
		    UsernameBox.this.setForeground(Color.BLACK);
		    UsernameBox.this.setText("");
		}
	    }
	});
	this.setColumns(10);
    }
}
