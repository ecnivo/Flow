package shared;

import gui.FlowClient;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

public class GenericConsole extends JTextArea {

    private String userInput;
    private String history = "FLOW - CONSOLE\n";
    private JPopupMenu popUp;
    private JScrollPane scrolling;

    public GenericConsole() {
	super();
	setWrapStyleWord(true);
	setBackground(Color.BLACK);
	setForeground(Color.WHITE);
	setFont(new Font("Consolas", Font.PLAIN, 12));
	setHighlighter(null);
	// TODO implement selecting (and delete/backspace key while selecting,
	// and typing... later.)
	setEditable(false);
	setBorder(FlowClient.EMPTY_BORDER);
	getCaret().setVisible(true);
	setCaretColor(Color.WHITE);
	scrolling = new JScrollPane(this);
	scrolling
		.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	popUp = new JPopupMenu();
	JMenuItem copyButton = new JMenuItem();
	copyButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		StringSelection target = new StringSelection(history
			+ userInput);
		Toolkit.getDefaultToolkit().getSystemClipboard()
			.setContents(target, target);
	    }
	});
	copyButton.setText("Copy");
	copyButton.setEnabled(true);
	popUp.add(copyButton);

	JMenuItem pasteButton = new JMenuItem();
	pasteButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		try {
		    String clip = "";
		    try {
			clip = Toolkit.getDefaultToolkit().getSystemClipboard()
				.getData(DataFlavor.stringFlavor).toString();
		    } catch (HeadlessException e) {
			e.printStackTrace();
		    } catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		    } catch (IOException e) {
			e.printStackTrace();
		    }
		    userInput += clip;
		    update();
		} catch (IllegalStateException e) {
		    e.printStackTrace();
		}
	    }
	});
	pasteButton.setText("Paste");
	pasteButton.setEnabled(true);
	popUp.add(pasteButton);
	getCaret().setSelectionVisible(true);
	setLineWrap(true);
	setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
	setMinimumSize(new Dimension(5, 25));
	userInput = "";
	getInputMap().put(KeyStroke.getKeyStroke("BACK_SPACE"), "none");
	getInputMap().put(KeyStroke.getKeyStroke("DELETE"), "none");
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
	addMouseListener(new MouseAdapter() {
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
    }

    private void update() {
	setText(history + userInput);
    }

    public void addOutput(String output) {
	history += output;
	update();
    }

    public JScrollPane getScroll() {
	return scrolling;
    }
}
