
package shared;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import callback.DocumentCallbackEvent;
import callback.TextModificationListener;
import editing.UserCaret;
import gui.FlowClient;
import message.Data;
import util.Formatter;

/**
 * The area for the user to edit their documents
 * 
 * @author Vince Ou
 *
 */
@SuppressWarnings("serial")
public class EditArea extends JTextPane {

	// Swing components
	private JScrollPane scrolling;
	private StyledDocument doc;

	// UUID trackers
	private UUID versionTextUUID;
	private UUID fileUUID;
	private UUID projectUUID;

	// Styles
	private Style keywordStyle;
	private Style plainStyle;
	private Style stringStyle;
	private Style commentsStyle;

	// "Blocks" of text to highlight
	private ArrayList<StyleBlock> keywordBlocks;
	private ArrayList<StyleBlock> stringBlocks;
	private ArrayList<StyleBlock> commentBlocks;

	// Different style constants
	public static final Font PLAIN = new Font("Consolas", Font.PLAIN, 13);
	public static final Color PLAIN_COLOUR = Color.BLACK;
	public static final Color KEYWORD_COLOUR = new Color(0x38761D);
	public static final Color STRING_COLOUR = new Color(0xA30BCF);
	public static final Color COMMENTS_COLOUR = new Color(0xD13313);

	// Java's keywords
	private static final String[] JAVA_KEYWORDS = { "abstract", "assert",
			"boolean", "break", "byte", "case", "catch", "char", "class",
			"const", "continue", "default", "do", "double", "else", "enum",
			"extends", "false", "final", "finally", "float", "for", "goto",
			"if", "implements", "import", "instanceof", "int", "interface",
			"long", "native", "new", "package", "private", "protected",
			"public", "return", "short", "static", "strictfp", "super",
			"switch", "synchronized", "this", "throws", "throw", "transient",
			"true", "try", "void", "volatile", "while" };

	private boolean ignoreEvents = false;
	private ArrayList<UserCaret> carets;

	/**
	 * Creates a new EditArea
	 * 
	 * @param textDoc
	 *            the text of the document to load on start
	 * @param projectUUID
	 *            the UUID of its parent project
	 * @param fileUUID
	 *            the UUID of the specific file
	 * @param versionTextUUID
	 *            the UUID of the version
	 * @param editable
	 *            if this should be editable
	 * @param tabs
	 *            the parent EditTabs
	 */
	public EditArea(String textDoc, UUID projectUUID, UUID fileUUID,
			UUID versionTextUUID, boolean editable, EditTabs tabs) {
		// Swing stuff
		setLayout(null);
		scrolling = new JScrollPane(this, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		setBorder(FlowClient.EMPTY_BORDER);
		setFont(PLAIN);
		setForeground(PLAIN_COLOUR);
		// Setup
		this.projectUUID = projectUUID;
		this.versionTextUUID = versionTextUUID;
		this.fileUUID = fileUUID;
		// Sets up the StyledDocument
		doc = getStyledDocument();
		setText(textDoc);
		doc.putProperty(PlainDocument.tabSizeAttribute, 4);
		setEditable(editable);

		// Asks the server for a list of editors to create their respective
		// carets
		Data editorListRequest = new Data("project_info");
		editorListRequest.put("project_uuid", projectUUID);
		editorListRequest.put("session_id", Communicator.getSessionID());
		Data editorListData = Communicator.communicate(editorListRequest);
		if (editorListData.get("status", String.class)
				.equals("ACCESS_DENIED")) {
			JOptionPane.showConfirmDialog(null,
					"You do not have sufficient permissions complete this operation.",
					"Access Denied", JOptionPane.DEFAULT_OPTION,
					JOptionPane.WARNING_MESSAGE);
			return;
		} else if (!editorListData.get("status", String.class).equals("OK")) {
			return;
		}
		carets = new ArrayList<UserCaret>();
		String[] editors = editorListData.get("editors", String[].class);
		for (String userName : editors) {
			if (!userName.equals(Communicator.getUsername())) {
				UserCaret caret = new UserCaret(userName, this);
				add(caret);
				carets.add(caret);
			}
		}
		String ownerName = editorListData.get("owner", String.class);
		if (!ownerName.equals(Communicator.getUsername())) {
			UserCaret ownerCaret = new UserCaret(ownerName, this);
			add(ownerCaret);
			carets.add(ownerCaret);
		}

		// Creates styles for each of the syntax highlighting items
		keywordStyle = addStyle("keywords", null);
		StyleConstants.setForeground(keywordStyle, KEYWORD_COLOUR);
		StyleConstants.setItalic(keywordStyle, true);

		plainStyle = addStyle("plain", null);
		StyleConstants.setForeground(plainStyle, PLAIN_COLOUR);
		StyleConstants.setBold(plainStyle, false);

		stringStyle = addStyle("strings", null);
		StyleConstants.setBold(stringStyle, false);
		StyleConstants.setForeground(stringStyle, STRING_COLOUR);

		commentsStyle = addStyle("comments", null);
		StyleConstants.setBold(commentsStyle, false);
		StyleConstants.setForeground(commentsStyle, COMMENTS_COLOUR);

		addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// nothing
			}

			/**
			 * Close tab shortcut
			 */
			@Override
			public void keyReleased(KeyEvent e) {
				if (!e.isControlDown()) {
					return;
				}
				if (e.getKeyCode() == KeyEvent.VK_W) {
					tabs.removeTabAt(tabs.getSelectedIndex());
				} else if (e.getKeyCode() == KeyEvent.VK_PAGE_UP
						&& tabs.getSelectedIndex() > 0) {
					System.out.println("switch left");
					tabs.setSelectedComponent(
							tabs.getComponentAt(tabs.getSelectedIndex() - 1));
				} else if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN
						&& tabs.getSelectedIndex() < tabs.getTabCount() - 1) {
					System.out.println("switch right");
					tabs.setSelectedComponent(
							tabs.getComponentAt(tabs.getSelectedIndex() + 1));
				}
				if (e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_F) {
					String text = EditArea.this.getText();
					Data fileModify = new Data("file_text_modify");
					fileModify.put("file_uuid", fileUUID);
					fileModify.put("session_id", Communicator.getSessionID());
					fileModify.put("mod_type", "DELETE");
					fileModify.put("idx", 0);
					fileModify.put("len", text.length());

					// Send message to server about what was inserted
					Data response = Communicator.communicate(fileModify);

					String form = Formatter.format(text);
					fileModify = new Data("file_text_modify");
					fileModify.put("file_uuid", fileUUID);
					fileModify.put("session_id", Communicator.getSessionID());
					fileModify.put("mod_type", "INSERT");
					fileModify.put("idx", 0);
					fileModify.put("str", form);

					// Send message to server about what was inserted
					response = Communicator.communicate(fileModify);
					EditArea.this.setText(form);

				}
			}

			/**
			 * Turns [TAB] into four spaces
			 */
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_TAB && editable) {
					try {
						doc.insertString(getCaretPosition(), "    ", null);
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
					e.consume();
				}
			}
		});
		doc.addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent e) {
				// useless for plaintext areas
			}

			/**
			 * For when stuff is inserted
			 */
			@Override
			public void insertUpdate(DocumentEvent e) {
				if (ignoreEvents || !editable)
					return;
				// Sets up some data that was inserted
				String insertedString = "";
				int strLen = e.getLength();
				int caretPos = e.getOffset();

				// Get the string
				try {
					insertedString = doc.getText(caretPos, strLen);
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
				// Prepare message for server to send text off
				Data fileModify = new Data("file_text_modify");
				fileModify.put("file_uuid", fileUUID);
				fileModify.put("session_id", Communicator.getSessionID());
				fileModify.put("mod_type", "INSERT");
				fileModify.put("idx", caretPos);
				fileModify.put("str", insertedString);

				// Send message to server about what was inserted
				Data response = Communicator.communicate(fileModify);
				String status = response.get("status", String.class);
				// Makes sure that things work
				if (response == null || status == null
						|| !status.equals("OK")) {
					JOptionPane.showConfirmDialog(null,
							"Your change to the file could not be processed.\nThis could be because the server is down, or your document is out of sync.\nTry closing this tab, opening it again, or restarting Flow.",
							"Failed to edit file", JOptionPane.DEFAULT_OPTION,
							JOptionPane.ERROR_MESSAGE);
					resetToServer();
					return;
				}
				// Updates syntax highlighting
				highlightSyntax();
			}

			/**
			 * For when stuff is deleted
			 */
			@Override
			public void removeUpdate(DocumentEvent e) {
				if (ignoreEvents || !editable)
					return;
				// Gets info on the change
				int removedLen = e.getLength();
				int caretPos = e.getOffset();

				// Creates message for server
				Data metadataModify = new Data("file_text_modify");
				metadataModify.put("file_uuid", fileUUID);
				metadataModify.put("session_id", Communicator.getSessionID());
				metadataModify.put("mod_type", "DELETE");
				metadataModify.put("idx", caretPos);
				metadataModify.put("len", removedLen);

				// Sends message to server
				Data response = Communicator.communicate(metadataModify);
				String status = response.get("status", String.class);
				if (response == null || status == null
						|| !status.equals("OK")) {
					JOptionPane.showConfirmDialog(null,
							"Your change to the file could not be processed.\nThis could be because the server is down, or your document is out of sync.\nTry closing this tab, opening it again, or restarting Flow.",
							"Failed to edit file", JOptionPane.DEFAULT_OPTION,
							JOptionPane.ERROR_MESSAGE);
					resetToServer();
					return;
				}
				// Highlights syntax
				highlightSyntax();
			}
		});
		// For when the user changes their caret position
		addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent e) {
				// Gets the caret position and sends to server
				int caretPos = e.getDot();
				Data caretPosChange = new Data("file_text_modify");
				caretPosChange.put("session_id", Communicator.getSessionID());
				caretPosChange.put("file_uuid", fileUUID);
				caretPosChange.put("mod_type", "MOVE");
				caretPosChange.put("idx", caretPos);
				Data response = Communicator.communicate(caretPosChange);
				if (response.get("status", String.class)
						.equals("ACCESS_DENIED")) {
					JOptionPane.showConfirmDialog(null,
							"You do not have sufficient permissions complete this operation.",
							"Access Denied", JOptionPane.DEFAULT_OPTION,
							JOptionPane.WARNING_MESSAGE);
					return;
				}
			}
		});
		// Listener to get changes from the server
		Communicator.addFileChangeListener(new TextModificationListener() {

			@Override
			public void onDocumentUpdate(DocumentCallbackEvent e) {
				// Prevents adding your own changes again
				if (e.USERNAME.equals(Communicator.getUsername())) {
					return;
				}

				switch (e.TYPE) {
				case INSERT:
					String addition = e.ADDITION;
					try {
						// Uses boolean flags when inserting or deleting so that
						// the
						// insertions/deletions don't interpret the other users'
						// inputs as the
						// current user's actions
						ignoreEvents = true;
						// Tries to insert the contents
						doc.insertString(e.INDEX, addition, null);
						ignoreEvents = false;
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
					break;

				case DELETE:
					int length = e.REMOVAL_LENGTH;
					try {
						ignoreEvents = true;
						// Tries to remove the contents
						doc.remove(e.INDEX, length);
						ignoreEvents = false;
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}

					fireCaretUpdate(new CaretEvent(EditArea.this) {

						@Override
						public int getMark() {
							return EditArea.this.getCaret().getMark();
						}

						@Override
						public int getDot() {
							return EditArea.this.getCaret().getDot();
						}
					});
					break;

				case MOVE:
					UserCaret caret = getCaretByUserName(e.USERNAME);
					if (caret == null) {
						System.out.println("caret not found");
						return;
					}
					Rectangle rectangle = null;
					try {
						rectangle = modelToView(e.INDEX);
					} catch (BadLocationException e1) {
						e1.printStackTrace();
						return;
					}
					caret.moveTo(rectangle.getLocation());
					repaint();

					break;

				default:
					break;
				}
				highlightSyntax();
			}

		}, fileUUID);
		// Update
		highlightSyntax();
	}

	private void resetToServer() {
		Data requestData = new Data("file_request");
		requestData.put("session_id", Communicator.getSessionID());
		requestData.put("file_uuid", fileUUID);
		Data response = Communicator.communicate(requestData);
		String status = response.get("status", String.class);
		if (response == null || status == null || !status.equals("OK")) {
			EditTabs tabs = (EditTabs) scrolling.getParent();
			if (tabs == null) {
				return;
			}
			tabs.remove(scrolling);
			return;
		}

		String text = new String(response.get("file_data", byte[].class));
		setText(text);
		highlightSyntax();
		revalidate();
		repaint();
	}

	/**
	 * Gets the VersionText's UUID
	 * 
	 * @return the versiontext's UUID
	 */
	public UUID getVersionTextUUID() {
		return versionTextUUID;
	}

	/**
	 * Gets the scrolling pane
	 * 
	 * @return the JScrollPane
	 */
	public JScrollPane getScrollPane() {
		return scrolling;
	}

	/**
	 * gets the project's UUID
	 * 
	 * @return the project's UUID
	 */
	public UUID getProjectUUID() {
		return projectUUID;
	}

	/**
	 * gets the file's UUID
	 * 
	 * @return the file's UUID
	 */
	public UUID getFileUUID() {
		return fileUUID;
	}

	/**
	 * Gets a caret by its username
	 * 
	 * @param name
	 *            name of user
	 * @return the caret that corresponds with the name. Returns null if not
	 *         found.
	 */
	private UserCaret getCaretByUserName(String name) {
		name = name.trim();
		for (UserCaret userCaret : carets) {
			if (userCaret.toString().equals(name)) {
				return userCaret;
			}
		}
		return null;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		Point mouse = getMousePosition();
		for (UserCaret userCaret : carets) {
			g2d.setColor(userCaret.getColor());
			g2d.fillRect((int) userCaret.getLocation().getX(),
					(int) userCaret.getLocation().getY(), 3, 17);
		}
	}

	/**
	 * A particular block of text that should be styled a certain way
	 * 
	 * @author Vince Ou
	 *
	 */
	private class StyleBlock {

		private int length;
		private int premiereIdx;

		/**
		 * Creates a new StyleBlock
		 * 
		 * @param length
		 *            the length of the block
		 * @param firstIdx
		 *            the index of the block
		 */
		private StyleBlock(int length, int firstIdx) {
			// Saves them
			this.length = length;
			this.premiereIdx = firstIdx;
		}

		/**
		 * Gets the length of the block
		 * 
		 * @return the length of the block
		 */
		private int getLength() {
			return length;
		}

		/**
		 * Gets the first index
		 * 
		 * @return the first index
		 */
		private int getFirstIdx() {
			return premiereIdx;
		}
	}

	/**
	 * WORKING syntax highlighting - Bimesh
	 */
	private void highlightSyntax() {
		// Creates new blocks
		keywordBlocks = new ArrayList<StyleBlock>();
		stringBlocks = new ArrayList<StyleBlock>();
		commentBlocks = new ArrayList<StyleBlock>();

		String text = this.getText(), word = "";
		printOut(text.toCharArray());
		int textLength = text.length(), spaceCount = 0;
		for (int i = 0; i < textLength; i++) {
			char c = text.charAt(i);
			if (Character.isAlphabetic(c) || (c + "").matches("[0-9]")) {
				word += c;
				if (i == textLength - 1)
					if (Arrays.asList(JAVA_KEYWORDS).contains(word)) {
						keywordBlocks.add(new StyleBlock(word.length(),
								i + 1 - word.length() - spaceCount));
					}
			} else {
				int length = word.length(), no;
				if (Arrays.asList(JAVA_KEYWORDS).contains(word)) {
					keywordBlocks.add(new StyleBlock(length,
							i - word.length() - spaceCount));
					if (c == 13)
						spaceCount++;
				} else if (c == '"') {
					boolean done = false;
					int start = i;
					for (i = i + 1; !done && i < textLength; i++) {
						c = text.charAt(i);
						if (c == 13)
							spaceCount++;
						else if (c == '\"') {
							stringBlocks.add(new StyleBlock(i - start,
									start - spaceCount));
							done = true;
						}
					}
					i--;
				} else if (c == '\'') {
					boolean done = false;
					int start = i;
					for (i = i + 1; !done && i < textLength; i++) {
						c = text.charAt(i);
						if (c == 13)
							spaceCount++;
						else if (c == '\'') {
							stringBlocks.add(new StyleBlock(i - start,
									start - spaceCount));
							done = true;
						}
					}
					i--;
				} else if (c == '/') {
					boolean done = false;
					if (text.charAt(Math.min(i + 1, textLength)) == '/') {
						int start = i;
						i += 2;
						for (; !done && i < textLength; i++) {
							c = text.charAt(i);
							if (c == 13)
								spaceCount++;
							else if (c == '\n') {
								done = true;
							}
						}
						commentBlocks.add(
								new StyleBlock(i - start, start - spaceCount));
						i--;
					} else if (text.charAt(i + 1) == '*') {
						int start = i, internalSpaceCount = 0;
						i += 2;
						for (; !done && i < textLength; i++) {
							c = text.charAt(i);
							if (c == 13)
								internalSpaceCount++;
							else if (c == '*') {
								i++;
								c = text.charAt(i);
								if (c == 13)
									internalSpaceCount++;
								else if (c == '/') {
									done = true;
								}
							}
						}
						commentBlocks.add(
								new StyleBlock(i - start - internalSpaceCount,
										start - spaceCount));
						spaceCount += internalSpaceCount;
						i--;
					}
				}
				if (c == 13)
					spaceCount++;
				word = "";
			}
		}

		// First paints everything "plain", then does key words, strings, then
		// comments
		SwingUtilities.invokeLater(new FormatPlainLater(0, textLength));

		for (StyleBlock styleBlock : keywordBlocks) {
			SwingUtilities.invokeLater(new FormatKeywordsLater(
					styleBlock.getFirstIdx(), styleBlock.getLength()));
		}
		for (StyleBlock styleBlock : stringBlocks) {
			SwingUtilities.invokeLater(new FormatStringsLater(
					styleBlock.getFirstIdx(), styleBlock.getLength() + 1));
		}
		for (StyleBlock styleBlock : commentBlocks) {
			SwingUtilities.invokeLater(new FormatCommentsLater(
					styleBlock.getFirstIdx(), styleBlock.getLength()));
		}
	}

	static void printOut(char[] chars) {
		for (char c : chars)
			System.out.print("'" + ((int) c) + "', ");
	}

	/**
	 * A runnable with data
	 * 
	 * @author Vince Ou
	 *
	 */
	private class FormatKeywordsLater implements Runnable {

		private int pos;
		private int nextToken;

		/**
		 * Creates a new FormatKeywordsLater
		 * 
		 * @param pos
		 *            the position
		 * @param nextToken
		 *            the length
		 */
		private FormatKeywordsLater(int pos, int nextToken) {

			this.pos = pos;
			this.nextToken = nextToken;
		}

		@Override
		public void run() {
			// Sets the attributes when it's run
			doc.setCharacterAttributes(pos, nextToken, keywordStyle, true);
		}

	}

	/**
	 * A runnable with data (copy of FormatKeywordsLater)
	 * 
	 * @author Vince
	 *
	 */
	private class FormatPlainLater implements Runnable {

		private int pos;
		private int nextToken;

		private FormatPlainLater(int pos, int nextToken) {
			this.pos = pos;
			this.nextToken = nextToken;
		}

		@Override
		public void run() {
			doc.setCharacterAttributes(pos, nextToken, plainStyle, true);
		}

	}

	/**
	 * A runnable with data (copy of FormatKeywordsLater)
	 * 
	 * @author Vince
	 *
	 */
	private class FormatStringsLater implements Runnable {

		private int pos;
		private int nextToken;

		private FormatStringsLater(int pos, int nextToken) {
			this.pos = pos;
			this.nextToken = nextToken;
		}

		@Override
		public void run() {
			doc.setCharacterAttributes(pos, nextToken, stringStyle, true);
		}
	}

	/**
	 * A runnable with data (copy of FormatKeywordsLater)
	 * 
	 * @author Vince
	 *
	 */
	private class FormatCommentsLater implements Runnable {

		private int pos;
		private int nextToken;

		private FormatCommentsLater(int pos, int nextToken) {
			this.pos = pos;
			this.nextToken = nextToken;
		}

		@Override
		public void run() {
			doc.setCharacterAttributes(pos, nextToken, commentsStyle, true);
		}
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return getUI().getPreferredSize(this).width <= getParent()
				.getSize().width;
	}
}
