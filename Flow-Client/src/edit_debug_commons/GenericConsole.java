package edit_debug_commons;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.text.DefaultEditorKit;

public class GenericConsole extends JTextArea {

    String userInput;
    String history = "FLOW - CONSOLE\n";
    JPopupMenu popUp;

    public GenericConsole() {
	super(50, 20);
	setWrapStyleWord(true);
	setBackground(Color.BLACK);
	setForeground(Color.WHITE);
	setFont(new Font("Consolas", Font.PLAIN, 12));
	setHighlighter(null);
	setEditable(false);
	getCaret().setVisible(true);
	setCaretColor(Color.WHITE);
	popUp = new JPopupMenu();
	JMenuItem copyButton = new JMenuItem(new Action() {

	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		StringSelection target = new StringSelection(history
			+ userInput);
		Toolkit.getDefaultToolkit().getSystemClipboard()
			.setContents(target, target);
	    }

	    @Override
	    public void setEnabled(boolean b) {
		// nothing

	    }

	    @Override
	    public void removePropertyChangeListener(
		    PropertyChangeListener listener) {
		// nothing

	    }

	    @Override
	    public void putValue(String key, Object value) {
		// nothing

	    }

	    @Override
	    public boolean isEnabled() {
		// nothing
		return false;
	    }

	    @Override
	    public Object getValue(String key) {
		// nothing
		return null;
	    }

	    @Override
	    public void addPropertyChangeListener(
		    PropertyChangeListener listener) {
		// nothing

	    }
	});
	copyButton.setText("Copy");
	copyButton.setEnabled(true);
	popUp.add(copyButton);

	JMenuItem pasteButton = new JMenuItem(new Action() {

	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		try {
		    userInput += Toolkit.getDefaultToolkit()
			    .getSystemClipboard().getContents(null);
		    update();
		} catch (IllegalStateException e) {
		    e.printStackTrace();
		}
	    }

	    @Override
	    public void setEnabled(boolean b) {
		// nothing

	    }

	    @Override
	    public void removePropertyChangeListener(
		    PropertyChangeListener listener) {
		// nothing

	    }

	    @Override
	    public void putValue(String key, Object value) {
		// nothing

	    }

	    @Override
	    public boolean isEnabled() {
		// nothing
		return false;
	    }

	    @Override
	    public Object getValue(String key) {
		// nothing
		return null;
	    }

	    @Override
	    public void addPropertyChangeListener(
		    PropertyChangeListener listener) {
		// nothing

	    }
	});
	pasteButton.setText("Paste");
	pasteButton.setEnabled(true);
	popUp.add(pasteButton);
	getCaret().setSelectionVisible(true);
	setLineWrap(true);
	getActionMap().get(DefaultEditorKit.deletePrevCharAction).setEnabled(
		false);
	getActionMap().get(DefaultEditorKit.deleteNextCharAction).setEnabled(
		false);
	setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
	setMinimumSize(new Dimension(5, 25));
	userInput = "";
	addKeyListener(new KeyListener() {

	    @Override
	    public void keyPressed(KeyEvent e) {
		// nothing
	    }

	    @Override
	    public void keyReleased(KeyEvent e) {
		// nothing
	    }

	    @Override
	    public void keyTyped(KeyEvent e) {
		if (e.isControlDown())
		    return;

		int pos = getCaret().getDot() - history.length();
		boolean setNewCaretPos = false;

		switch (e.getKeyChar()) {
		case (KeyEvent.VK_ENTER):
		    sendCommand(userInput);
		    history += userInput + "\n";
		    userInput = "";
		    break;
		case (KeyEvent.VK_BACK_SPACE):
		    if (pos <= userInput.length() && pos > 0) {
			userInput = userInput.substring(0, pos - 1)
				+ userInput.substring(pos, userInput.length());
			pos--;
			setNewCaretPos = true;
		    } else if (pos == 0) {
			return;
		    }
		    break;
		case (KeyEvent.VK_DELETE):
		    if (pos < userInput.length() && pos >= 0) {
			userInput = userInput.substring(0, pos)
				+ userInput.substring(pos + 1,
					userInput.length());
			setNewCaretPos = true;
		    }
		    break;
		default:
		    if ((int) (e.getKeyChar()) != -1) {
			if (pos < userInput.length() && pos > 0) {
			    userInput = userInput.substring(0, pos)
				    + e.getKeyChar()
				    + userInput.substring(pos,
					    userInput.length());
			    setNewCaretPos = true;
			} else if (pos <= 0) {
			    userInput = e.getKeyChar() + userInput;
			    setNewCaretPos = true;
			} else {
			    userInput += e.getKeyChar();
			}
			pos++;
		    }
		    break;
		}

		update();
		if (setNewCaretPos)
		    getCaret().setDot(pos + history.length());
	    }
	    // TODO add right click menu: copy, paste, export current console
	    // text
	});
	addFocusListener(new FocusListener() {

	    @Override
	    public void focusLost(FocusEvent e) {
		getCaret().setVisible(true);
	    }

	    @Override
	    public void focusGained(FocusEvent e) {
		getCaret().setVisible(true);
	    }
	});
	addMouseListener(new MouseListener() {

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
		if (e.getButton() == MouseEvent.BUTTON3) {
		    popUp.show(GenericConsole.this, e.getX(), e.getY());
		}
	    }
	});
	update();
    }

    private void sendCommand(String command) {
	// TODO send command to the standard in
	System.out.println("Sending " + userInput + " to standard in");
    }

    private void update() {
	setText(history + userInput);
    }

    public void addOutput(String output) {
	history += output;
	update();
    }
}
