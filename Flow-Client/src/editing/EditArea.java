package editing;

import gui.FlowClient;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;

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

public class EditArea extends JTextPane {
    private JScrollPane scrolling;
    private StyledDocument doc;

    private Style keywordStyle;
    private Style plainStyle;
    private Style stringStyle;

    private ArrayList<StyleToken> keywordBlocks;
    private ArrayList<StyleToken> plainBlocks;
    private ArrayList<StyleToken> strings;

    private static final String[] JAVA_KEYWORDS = { "abstract", "assert",
	    "boolean", "break", "byte", "case", "catch", "char", "class",
	    "const", "continue", "default", "do", "double", "else", "enum",
	    "extends", "final", "finally", "float", "for", "goto", "if",
	    "implements", "import", "instanceof", "int", "interface", "long",
	    "native", "new", "package", "private", "protected", "public",
	    "return", "short", "static", "strictfp", "super", "switch",
	    "synchronized", "this", "throws", "throw", "transient", "try",
	    "void", "volatile", "while" };

    protected EditArea(File file, boolean editable, EditTabs tabs) {
	scrolling = new JScrollPane(EditArea.this);
	setBorder(FlowClient.EMPTY_BORDER);
	setFont(new Font("Consolas", Font.PLAIN, 13));
	doc = (StyledDocument) getDocument();
	doc.putProperty(PlainDocument.tabSizeAttribute, 4);
	setEditable(editable);

	keywordStyle = addStyle("keywords", null);
	StyleConstants.setForeground(keywordStyle, new Color(0x006C79));
	StyleConstants.setBold(keywordStyle, true);
	plainStyle = addStyle("plain", null);
	StyleConstants.setForeground(plainStyle, Color.BLACK);
	StyleConstants.setBold(plainStyle, false);
	stringStyle = addStyle("strings", null);
	StyleConstants.setBold(stringStyle, false);
	StyleConstants.setForeground(stringStyle, new Color(0x45AD00));

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
		if (e.getKeyCode() == KeyEvent.VK_TAB) {
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

	    // TODO if the server denies the request, add the string back/delete
	    // the added string without making the change

	    @Override
	    public void changedUpdate(DocumentEvent e) {
		// useless for plaintext areas
	    }

	    @Override
	    public void insertUpdate(DocumentEvent e) {
		String insertedString = "";
		try {
		    insertedString = doc.getText(
			    EditArea.this.getCaretPosition(), e.getLength());
		} catch (BadLocationException e1) {
		    e1.printStackTrace();
		}
		highlightSyntax();
		// System.out.println(insertedString + " was added at position "
		// + EditArea.this.getCaret().getDot());

		// TODO send this change to server
	    }

	    @Override
	    public void removeUpdate(DocumentEvent e) {
		int removedLen = e.getLength();
		highlightSyntax();
		// System.out.println("String of length " + removedLen
		// + " was removed at "
		// + EditArea.this.getCaret().getDot());

		// TODO send this change to server
	    }
	});
	addCaretListener(new CaretListener() {

	    @Override
	    public void caretUpdate(CaretEvent arg0) {
		int caretPos = getCaret().getDot();
		// System.out.println("Caret moved to "
		// + EditArea.this.getCaret().getDot());
		// TODO send position to server
	    }
	});
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
	strings = new ArrayList<StyleToken>();

	String sourceCode = getText();
	int sourceLength = sourceCode.length();
	int line;
	for (String target : JAVA_KEYWORDS) {
	    int targetLength = target.length();
	    line = 0;
	    for (int pos = 0; pos + targetLength < sourceLength; pos++) {
		if (pos > 0 && pos + targetLength + 1 < sourceLength) {
		    if (!Character.isAlphabetic(sourceCode.charAt(pos - 1))
			    && !Character.isAlphabetic(sourceCode.charAt(pos
				    + targetLength))) {
			markWordBlocks(sourceCode, pos, target, line);
		    }
		} else if (pos == 0 && pos + targetLength + 1 < sourceLength) {
		    if (!Character.isAlphabetic(sourceCode.charAt(pos
			    + targetLength)))
			markWordBlocks(sourceCode, pos, target, line);
		} else if (pos > 0 && pos + targetLength + 1 == sourceLength) {
		    if (!Character.isAlphabetic(sourceCode.charAt(pos - 1)))
			markWordBlocks(sourceCode, pos, target, line);
		} else if (pos == 0 && pos + targetLength + 1 == sourceLength)
		    markWordBlocks(sourceCode, pos, target, line);

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

		    while (closeQuote > 0
			    && sourceCode.charAt(closeQuote - 1) == '\\') {
			closeQuote = sourceCode.indexOf('"', closeQuote + 1);
		    }
		    if (closeQuote > -1) {
			strings.add(new StyleToken(closeQuote - pos, pos - line));
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
		    while (closeQuote > 0
			    && sourceCode.charAt(closeQuote - 1) == '\\') {
			closeQuote = sourceCode.indexOf('\'', closeQuote + 1);
		    }
		    if (closeQuote > -1) {
			strings.add(new StyleToken(closeQuote - pos, pos - line));
			pos = closeQuote;
		    }
		}
	    }

	    if (sourceCode.charAt(pos) == '\n') {
		line++;
	    }
	}

	for (StyleToken styleToken : plainBlocks) {
	    SwingUtilities.invokeLater(new FormatPlainLater(
		    styleToken.getPos(), styleToken.getLength()));
	}
	for (StyleToken styleToken : keywordBlocks) {
	    SwingUtilities.invokeLater(new FormatKeywordsLater(styleToken
		    .getPos(), styleToken.getLength()));
	}
	for (StyleToken styleToken : strings) {
	    SwingUtilities.invokeLater(new FormatStringsLater(styleToken
		    .getPos(), styleToken.getLength() + 1));
	}
    }

    private boolean arrayContains(String[] array, String target) {
	for (String string : array) {
	    if (string.equals(target))
		return true;
	}
	return false;
    }

    private void markWordBlocks(String sourceCode, int pos, String target,
	    int line) {
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
}