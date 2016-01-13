package shared;

import editing.UserCaret;
import gui.FlowClient;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
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
import struct.FlowDocument;
import struct.FlowProject;
import struct.TextDocument;
import struct.User;

@SuppressWarnings("serial")
public class EditArea extends JTextPane {
    private JScrollPane scrolling;
    private StyledDocument doc;
    private TextDocument document;

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
    public static final Color KEYWORD_COLOUR = new Color(0x9213D1);
    public static final Color STRING_COLOUR = new Color(0xA30BCF);
    public static final Color COMMENTS_COLOUR = new Color(0xD13313);

    private static final String[] JAVA_KEYWORDS = { "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throws", "throw", "transient", "try", "void", "volatile", "while" };

    public EditArea(TextDocument textDoc, boolean editable, EditTabs tabs) {
	setLayout(null);
	scrolling = new JScrollPane(EditArea.this);
	setBorder(FlowClient.EMPTY_BORDER);
	setFont(PLAIN);
	setForeground(PLAIN_COLOUR);
	this.document = textDoc;
	doc = (StyledDocument) new File(textDoc.getDocumentText());
	setStyledDocument(doc);
	setText(textDoc.getDocumentText());
	doc.putProperty(PlainDocument.tabSizeAttribute, 4);
	setEditable(editable);

	Iterator<User> userIt = ((FlowProject) textDoc.getParentFile().getParentDirectory().getRootDirectory()).getEditors().iterator();
	while (userIt.hasNext()) {
	    UserCaret caret = new UserCaret(userIt.next(), this);
	    add(caret);
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
		try {
		    insertedString = doc.getText(EditArea.this.getCaretPosition() - e.getLength(), e.getLength());
		} catch (BadLocationException e1) {
		    e1.printStackTrace();
		}
		Data documentModify = new Data("text_document_modify");
		documentModify.put("project", ((FlowProject) document.getParentFile().getParentDirectory().getRootDirectory()).getProjectUUID());
		documentModify.put("document", document.getUUID());
		documentModify.put("mod_type", "INSERT");

		int lastNewLine;
		try {
		    lastNewLine = doc.getText(0, EditArea.this.getCaretPosition() - e.getLength()).lastIndexOf('\n');
		} catch (BadLocationException e1) {
		    e1.printStackTrace();
		    return;
		}
		String text = getText();
		int lines = 0;
		for (int i = 0; i < lastNewLine; i++) {
		    if (Character.isWhitespace(text.charAt(i)))
			lines++;
		}
		documentModify.put("line", lines);
		documentModify.put("idx", EditArea.this.getCaretPosition() - e.getLength() - lastNewLine);
		documentModify.put("str", insertedString);

		Data response = Communicator.communicate(documentModify);
		String status = response.get("status", String.class);
		if (!status.equals("OK")) {
		    return;
		}

		highlightSyntax();
	    }

	    @Override
	    public void removeUpdate(DocumentEvent e) {
		int removedLen = e.getLength();

		Data documentModify = new Data("text_document_modify");
		documentModify.put("project", ((FlowProject) document.getParentFile().getParentDirectory().getRootDirectory()).getProjectUUID());
		documentModify.put("document", document.getUUID());
		documentModify.put("mod_type", "DELETE");

		int lastNewLine;
		try {
		    lastNewLine = doc.getText(0, EditArea.this.getCaretPosition()).lastIndexOf('\n');
		} catch (BadLocationException e1) {
		    e1.printStackTrace();
		    return;
		}
		String text = getText();
		int lines = 0;
		for (int i = 0; i < lastNewLine; i++) {
		    if (Character.isWhitespace(text.charAt(i)))
			lines++;
		}
		documentModify.put("line", lines);
		documentModify.put("idx", EditArea.this.getCaretPosition() - lastNewLine);
		documentModify.put("len", removedLen);

		Data response = Communicator.communicate(documentModify);
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
	highlightSyntax();
    }

    public FlowDocument getFlowDoc() {
	return document;
    }

    public JScrollPane getScrollPane() {
	return scrolling;
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
	    doc.setCharacterAttributes(styleToken.getPos(), styleToken.getLength(), keywordStyle, false);
	    // SwingUtilities.invokeLater(new FormatPlainLater(
	    // styleToken.getPos(), styleToken.getLength()));
	}
	for (StyleToken styleToken : keywordBlocks) {
	    doc.setCharacterAttributes(styleToken.getPos(), styleToken.getLength(), keywordStyle, false);
	    // SwingUtilities.invokeLater(new FormatKeywordsLater(styleToken
	    // .getPos(), styleToken.getLength()));
	}
	for (StyleToken styleToken : stringBlocks) {
	    doc.setCharacterAttributes(styleToken.getPos(), styleToken.getLength() + 1, stringStyle, false);
	    // SwingUtilities.invokeLater(new FormatStringsLater(styleToken
	    // .getPos(), styleToken.getLength() + 1));
	}
	for (StyleToken token : commentBlocks) {
	    doc.setCharacterAttributes(token.getPos(), token.getLength() + 1, stringStyle, false);
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

    // private class FormatKeywordsLater implements Runnable {
    //
    // private int pos;
    // private int nextToken;
    //
    // private FormatKeywordsLater(int pos, int nextToken) {
    // this.pos = pos;
    // this.nextToken = nextToken;
    // }
    //
    // @Override
    // public void run() {
    // doc.setCharacterAttributes(pos, nextToken, keywordStyle, false);
    // }
    //
    // }
    //
    // private class FormatPlainLater implements Runnable {
    //
    // private int pos;
    // private int nextToken;
    //
    // private FormatPlainLater(int pos, int nextToken) {
    // this.pos = pos;
    // this.nextToken = nextToken;
    // }
    //
    // @Override
    // public void run() {
    // doc.setCharacterAttributes(pos, nextToken, plainStyle, false);
    // }
    //
    // }
    //
    // private class FormatStringsLater implements Runnable {
    // private int pos;
    // private int nextToken;
    //
    // private FormatStringsLater(int pos, int nextToken) {
    // this.pos = pos;
    // this.nextToken = nextToken;
    // }
    //
    // @Override
    // public void run() {
    // doc.setCharacterAttributes(pos, nextToken, stringStyle, false);
    // }
    // }
}