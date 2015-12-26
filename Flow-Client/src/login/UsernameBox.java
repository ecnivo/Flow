package login;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

public class UsernameBox extends JTextField {

    private static final String DEFAULT_TEXT = "Username";
    private static final Color DESELECT = Color.GRAY;

    public UsernameBox() {
	setPreferredSize(new Dimension(128, 32));
	this.setToolTipText("Your Flow username");
	this.setText(DEFAULT_TEXT);
	this.setForeground(DESELECT);
	this.addFocusListener(new FocusListener() {

	    @Override
	    public void focusLost(FocusEvent e) {
		UsernameBox box = UsernameBox.this;
		if (box.getText().trim().equals("")) {
		    box.setForeground(DESELECT);
		    box.setText(DEFAULT_TEXT);
		}
	    }

	    @Override
	    public void focusGained(FocusEvent e) {
		UsernameBox box = UsernameBox.this;
		if (box.getText().trim().equals("")
			|| box.getText().equals(DEFAULT_TEXT)) {
		    box.setForeground(Color.BLACK);
		    box.setText("");
		}
	    }
	});
	this.setColumns(10);
    }
}
