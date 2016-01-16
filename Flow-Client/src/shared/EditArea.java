package shared;

import editing.UserCaret;
import gui.FlowClient;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.UUID;

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

import message.Data;
import callback.DocumentCallbackEvent;
import callback.TextModificationListener;

@SuppressWarnings("serial")
public class EditArea extends JTextPane {
    private JScrollPane scrolling;
    private StyledDocument doc;
    private UUID versionTextUUID;
    private UUID projectUUID;

    private Style keywordStyle;
    private Style plainStyle;
    private Style stringStyle;
    private Style commentsStyle;

    private ArrayList<StyleToken> keywordBlocks;
    private ArrayList<StyleToken> plainBlocks;
    private ArrayList<StyleToken> stringBlocks;
    private ArrayList<StyleToken> commentBlocks;

    public static final Font PLAIN = new Font("Consolas", Font.PLAIN, 13);
    public static final Color PLAIN_COLOUR = Color.BLACK;
    public static final Color KEYWORD_COLOUR = new Color(0x38761D);
    public static final Color STRING_COLOUR = new Color(0xA30BCF);
    public static final Color COMMENTS_COLOUR = new Color(0xD13313);

    private static final String[] JAVA_KEYWORDS = { "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throws", "throw", "transient", "try", "void", "volatile", "while" };

    public EditArea(String textDoc, UUID projectUUID, UUID fileUUID, UUID versionTextUUID, boolean editable, EditTabs tabs) {
	setLayout(null);
	this.projectUUID = projectUUID;
	this.versionTextUUID = versionTextUUID;
	scrolling = new JScrollPane(this);
	setBorder(FlowClient.EMPTY_BORDER);
	setFont(PLAIN);
	setForeground(PLAIN_COLOUR);
	doc = getStyledDocument();
	setText(textDoc);
	doc.putProperty(PlainDocument.tabSizeAttribute, 4);
	setEditable(editable);

	Data editorListRequest = new Data("project_info");
	editorListRequest.put("project_uuid", projectUUID);
	Data editorListData = Communicator.communicate(editorListRequest);
	if (!editorListData.get("status", String.class).equals("OK")) {
	    return;
	}
	String[] editors = editorListData.get("editors", String[].class);
	for (String editor : editors) {
	    add(new UserCaret(editor, this));
	}

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

	    @Override
	    public void keyReleased(KeyEvent e) {
		if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_W) {
		    tabs.removeTabAt(tabs.getSelectedIndex());
		}
	    }

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

	    @Override
	    public void insertUpdate(DocumentEvent e) {
		String insertedString = "";
		int strLen = e.getLength();
		int startPos = getCaretPosition();
		System.out.println("styledoc pos at " + startPos + " length " + strLen);
		try {
		    insertedString = doc.getText(startPos, strLen);
		} catch (BadLocationException e1) {
		    e1.printStackTrace();
		}
		Data fileModify = new Data("file_text_modify");
		fileModify.put("file_uuid", fileUUID);
		fileModify.put("mod_type", "INSERT");

		int lastNewLine;
		try {
		    lastNewLine = doc.getText(0, startPos).lastIndexOf('\n');
		} catch (BadLocationException e1) {
		    e1.printStackTrace();
		    return;
		}
		String text = getText();
		int lines = 0;
		for (int i = 0; i <= lastNewLine; i++) {
		    if (text.charAt(i) == '\n')
			lines++;
		}
		fileModify.put("line", lines);
		int idx;
		if (lastNewLine != -1)
		    idx = getCaretPosition() - lastNewLine;
		else
		    idx = startPos;
		fileModify.put("idx", idx);
		fileModify.put("str", insertedString);

		System.out.println("inserted string " + insertedString + " at line " + lines + " at index " + idx);

		Data response = Communicator.communicate(fileModify);
		String status = response.get("status", String.class);
		if (!status.equals("OK")) {
		    return;
		}

		highlightSyntax();
	    }

	    @Override
	    public void removeUpdate(DocumentEvent e) {
		int removedLen = e.getLength();
		int caretPos = getCaretPosition() - removedLen;

		Data metadataModify = new Data("file_metadata_modify");
		metadataModify.put("file_uuid", fileUUID);
		metadataModify.put("session_id", Communicator.getSessionID());
		metadataModify.put("mod_type", "DELETE");

		int lastNewLine;
		try {
		    lastNewLine = doc.getText(0, caretPos).lastIndexOf('\n');
		} catch (BadLocationException e1) {
		    e1.printStackTrace();
		    return;
		}
		String text = getText();
		int lines = 0;
		for (int i = 0; i < lastNewLine; i++) {
		    if (text.charAt(i) == '\n')
			lines++;
		}
		metadataModify.put("line", lines);
		metadataModify.put("idx", caretPos - lastNewLine);
		metadataModify.put("len", removedLen);

		Data response = Communicator.communicate(metadataModify);
		String status = response.get("status", String.class);
		if (!status.equals("OK")) {
		    return;
		}

		highlightSyntax();
	    }
	});
	addCaretListener(new CaretListener() {

	    @Override
	    public void caretUpdate(CaretEvent arg0) {
		int caretPos = getCaret().getDot();
		// TODO send position to server
	    }
	});
	TextModificationListener fileChangeListener = new TextModificationListener() {

	    @Override
	    public void onDocumentUpdate(DocumentCallbackEvent event) {
		int line = event.LINE;
		int idx = event.INDEX;

		String text = getText();
		int ln = 0;
		int posOfChange = 0;
		while (ln < line) {
		    if (text.charAt(posOfChange) == '\n') {
			ln++;
		    }
		    posOfChange++;
		}
		posOfChange += idx;

		if (event.TYPE == DocumentCallbackEvent.DocumentCallbackType.INSERT) {
		    String addition = event.ADDITION;
		    try {
			doc.insertString(posOfChange, addition, null);
		    } catch (BadLocationException e) {
			e.printStackTrace();
		    }
		} else if (event.TYPE == DocumentCallbackEvent.DocumentCallbackType.DELETE) {
		    int length = event.REMOVAL_LENGTH;
		    try {
			doc.remove(posOfChange, length);
		    } catch (BadLocationException e) {
			e.printStackTrace();
		    }
		}
	    }

	};
	Communicator.addFileChangeListener(fileChangeListener, fileUUID);
	highlightSyntax();
    }

    public UUID getVersionTextUUID() {
	return versionTextUUID;
    }

    public JScrollPane getScrollPane() {
	return scrolling;
    }

    public UUID getProjectUUID() {
	return projectUUID;
    }

    private class StyleToken {
	private int length;
	private int pos;

	private StyleToken(int length, int pos) {
	    this.length = length;
	    this.pos = pos;
	}

	private int getLength() {
	    return length;
	}

	private int getPos() {
	    return pos;
	}
    }

    private void highlightSyntax() {
	keywordBlocks = new ArrayList<StyleToken>();
	plainBlocks = new ArrayList<StyleToken>();
	stringBlocks = new ArrayList<StyleToken>();
	commentBlocks = new ArrayList<StyleToken>();

	String sourceCode = getText();
	int sourceLength = sourceCode.length();
	int line;
	for (String target : JAVA_KEYWORDS) {
	    int targetLength = target.length();
	    line = 0;
	    for (int pos = 0; pos + targetLength < sourceLength; pos++) {
		if (pos > 0 && pos + targetLength + 1 < sourceLength) {
		    if (!Character.isAlphabetic(sourceCode.charAt(pos - 1)) && !Character.isAlphabetic(sourceCode.charAt(pos + targetLength))) {
			markWordBlocks(sourceCode, pos, target, line);
			pos += targetLength;
		    }
		} else if (pos == 0 && pos + targetLength + 1 < sourceLength) {
		    if (!Character.isAlphabetic(sourceCode.charAt(pos + targetLength))) {
			markWordBlocks(sourceCode, pos, target, line);
			pos += targetLength;
		    }
		} else if (pos > 0 && pos + targetLength + 1 == sourceLength) {
		    if (!Character.isAlphabetic(sourceCode.charAt(pos - 1))) {
			markWordBlocks(sourceCode, pos, target, line);
			pos += targetLength;
		    }
		} else if (pos == 0 && pos + targetLength + 1 == sourceLength) {
		    markWordBlocks(sourceCode, pos, target, line);
		    pos += targetLength;
		}
		if (sourceCode.charAt(pos) == '\n') {
		    line++;
		}
	    }
	}

	line = 0;
	for (int pos = 0; pos < sourceLength; pos++) {
	    if (sourceCode.charAt(pos) == '"') {
		boolean escaped;
		if ((pos > 0 && sourceCode.charAt(pos - 1) != '\\') || pos == 0) {
		    escaped = false;
		} else {
		    escaped = true;
		}
		if (!escaped) {
		    int closeQuote = sourceCode.indexOf('"', pos + 1);

		    while (closeQuote > 0 && sourceCode.charAt(closeQuote - 1) == '\\') {
			closeQuote = sourceCode.indexOf('"', closeQuote + 1);
		    }
		    if (closeQuote > -1) {
			stringBlocks.add(new StyleToken(closeQuote - pos, pos - line));
			pos = closeQuote;
		    }
		}
	    } else if (sourceCode.charAt(pos) == '\'') {
		boolean escaped;
		if ((pos > 0 && sourceCode.charAt(pos - 1) != '\\') || pos == 0) {
		    escaped = false;
		} else {
		    escaped = true;
		}
		if (!escaped) {
		    int closeQuote = sourceCode.indexOf('\'', pos + 1);
		    while (closeQuote > 0 && sourceCode.charAt(closeQuote - 1) == '\\') {
			closeQuote = sourceCode.indexOf('\'', closeQuote + 1);
		    }
		    if (closeQuote > -1) {
			stringBlocks.add(new StyleToken(closeQuote - pos, pos - line));
			pos = closeQuote;
		    }
		}
	    }

	    if (sourceCode.charAt(pos) == '\n') {
		line++;
	    }
	}

	for (int pos = 0; pos < sourceLength; pos++) {
	    if (sourceCode.substring(pos, pos + 1).equals("////")) {
		int nextLine = sourceCode.indexOf('\n', pos);
		StyleToken commentToken = new StyleToken((nextLine - pos), pos);
		commentBlocks.add(commentToken);
		pos = nextLine;
	    } else if (sourceCode.substring(pos, pos + 1).equals("//*")) {
		int end = sourceCode.indexOf("*//", pos);
		StyleToken commentToken = new StyleToken((end - pos), pos);
		commentBlocks.add(commentToken);
		pos = end;
	    }
	}

	for (StyleToken styleToken : plainBlocks) {
	    // doc.setCharacterAttributes(styleToken.getPos(),
	    // styleToken.getLength(), keywordStyle, false);
	    SwingUtilities.invokeLater(new FormatPlainLater(styleToken.getPos(), styleToken.getLength()));
	}
	for (StyleToken styleToken : keywordBlocks) {
	    // doc.setCharacterAttributes(styleToken.getPos(),
	    // styleToken.getLength(), keywordStyle, false);
	    SwingUtilities.invokeLater(new FormatKeywordsLater(styleToken.getPos(), styleToken.getLength()));
	}
	for (StyleToken styleToken : stringBlocks) {
	    // doc.setCharacterAttributes(styleToken.getPos(),
	    // styleToken.getLength() + 1, stringStyle, false);
	    SwingUtilities.invokeLater(new FormatStringsLater(styleToken.getPos(), styleToken.getLength() + 1));
	}
	for (StyleToken token : commentBlocks) {
	    // doc.setCharacterAttributes(token.getPos(), token.getLength() + 1,
	    // stringStyle, false);
	    SwingUtilities.invokeLater(new FormatCommentsLater(token.getPos(), token.getLength()));
	}
    }

    private boolean arrayContains(String[] array, String target) {
	for (String string : array) {
	    if (string.equals(target))
		return true;
	}
	return false;
    }

    private void markWordBlocks(String sourceCode, int pos, String target, int line) {
	String candidate = sourceCode.substring(pos, pos + target.length());
	if (arrayContains(JAVA_KEYWORDS, candidate))
	    keywordBlocks.add(new StyleToken(candidate.length(), pos - line));
	else
	    plainBlocks.add(new StyleToken(candidate.length(), pos - line));
    }

    private class FormatKeywordsLater implements Runnable {

	private int pos;
	private int nextToken;

	private FormatKeywordsLater(int pos, int nextToken) {
	    this.pos = pos;
	    this.nextToken = nextToken;
	}

	@Override
	public void run() {
	    doc.setCharacterAttributes(pos, nextToken, keywordStyle, false);
	}

    }

    private class FormatPlainLater implements Runnable {

	private int pos;
	private int nextToken;

	private FormatPlainLater(int pos, int nextToken) {
	    this.pos = pos;
	    this.nextToken = nextToken;
	}

	@Override
	public void run() {
	    doc.setCharacterAttributes(pos, nextToken, plainStyle, false);
	}

    }

    private class FormatStringsLater implements Runnable {
	private int pos;
	private int nextToken;

	private FormatStringsLater(int pos, int nextToken) {
	    this.pos = pos;
	    this.nextToken = nextToken;
	}

	@Override
	public void run() {
	    doc.setCharacterAttributes(pos, nextToken, stringStyle, false);
	}
    }

    private class FormatCommentsLater implements Runnable {
	private int pos;
	private int nextToken;

	private FormatCommentsLater(int pos, int nextToken) {
	    this.pos = pos;
	    this.nextToken = nextToken;
	}

	@Override
	public void run() {
	    doc.setCharacterAttributes(pos, nextToken, commentsStyle, false);
	}
    }
}