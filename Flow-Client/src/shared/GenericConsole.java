
package shared;

import gui.FlowClient;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A console to run code in.
 * 
 * @author Vince Ou
 *
 */
@SuppressWarnings("serial")
public class GenericConsole extends JTextArea {

	private String		userInput;
	private String		history	= "FLOW - CONSOLE\n";
	private final JPopupMenu popUp;
	private final JScrollPane scrolling;
	private OutputStream activeOutputStream;

	/**
	 * Creates a new GenericConsole
	 */
	public GenericConsole() {
		// Swing things.
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

		// Sets up caret
		getCaret().setVisible(true);
		setCaretColor(Color.WHITE);

		// And scrolling
		scrolling = new JScrollPane(this);
		scrolling.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		// and pop-up menu
		popUp = new JPopupMenu();
		JMenuItem copyButton = new JMenuItem();
		// Creates a "copy" button
		copyButton.addActionListener(new ActionListener() {

			/**
			 * Copies the history and user input into the system clipboard
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				StringSelection target = new StringSelection(history + userInput);
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(target, target);
			}
		});
		copyButton.setText("Copy");
		copyButton.setEnabled(true);
		popUp.add(copyButton);

		// Creates a paste button
		JMenuItem pasteButton = new JMenuItem();
		pasteButton.addActionListener(new ActionListener() {

			/**
			 * Puts the system clipboard contents into the user input
			 */
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					String clip = "";
					try {
						clip = Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString();
					} catch (HeadlessException | IOException | UnsupportedFlavorException e) {
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

		// See the caret selection
		getCaret().setSelectionVisible(true);

		// More swing setup
		setLineWrap(true);
		setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		setMinimumSize(new Dimension(5, 25));
		userInput = "";

		// Make backspace and delete keys not work
		getInputMap().put(KeyStroke.getKeyStroke("BACK_SPACE"), "none");
		getInputMap().put(KeyStroke.getKeyStroke("DELETE"), "none");

		// Sanitize inputs into console
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
				// Block all [CTRL] events
				if (e.isControlDown())
					return;

				// Fun index stuff
				int pos = getCaret().getDot() - history.length();
				boolean setNewCaretPos = false;

				switch (e.getKeyChar()) {
				// Executes/feeds it to the runner
					case (KeyEvent.VK_ENTER):
						sendCommand(userInput);
						history += userInput + "\n";
						userInput = "";
						break;
					// Deletes stuff...?
					case (KeyEvent.VK_BACK_SPACE):
						if (pos <= userInput.length() && pos > 0) {
							userInput = userInput.substring(0, pos - 1) + userInput.substring(pos, userInput.length());
							pos--;
							setNewCaretPos = true;
						} else if (pos == 0) {
							return;
						}
						break;
					// Deletes more stuff
					case (KeyEvent.VK_DELETE):
						if (pos < userInput.length() && pos >= 0) {
							userInput = userInput.substring(0, pos) + userInput.substring(pos + 1, userInput.length());
							setNewCaretPos = true;
						}
						break;
					// If it's an actual letter, inserts it
					default:
						if ((int) (e.getKeyChar()) != -1) {
							if (pos < userInput.length() && pos > 0) {
								userInput = userInput.substring(0, pos) + e.getKeyChar() + userInput.substring(pos, userInput.length());
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
				// Throw the caret around because why not
				if (setNewCaretPos)
					getCaret().setDot(pos + history.length());
			}
		});
		// Set caret visible at all times
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
		// Allows right click menu
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					// Show popup menu when right clicked
					popUp.show(GenericConsole.this, e.getX(), e.getY());
				}
			}
		});
		update();
	}

	/**
	 * Sends the command to the runner
	 * @param command the command
	 */
	private void sendCommand(String command) {
		try {
			if (activeOutputStream != null) {
				System.out.println("writing " + command);
				activeOutputStream.write((command + "\n").getBytes());
				activeOutputStream.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setActiveOutputStream(OutputStream activeOutputStream) {
		this.activeOutputStream = activeOutputStream;
	}
	/**
	 * Updates the console window
	 */
	private void update() {
		setText(history + userInput);
	}

	/**
	 * Adds output to the console
	 * @param output the new output to add
	 */
	public void addOutput(String output) {
		history += output;
		update();
	}

	/**
	 * Gets the JScrollPane
	 * @return the scrolling of the console
	 */
	public JScrollPane getScroll() {
		return scrolling;
	}
}
