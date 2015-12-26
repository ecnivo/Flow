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
    private Style keywords;
    private Style plain;
    private ArrayList<StyleToken> blocks;
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

	keywords = addStyle("keywords", null);
	StyleConstants.setForeground(keywords, new Color(0x006C79));
	StyleConstants.setBold(keywords, true);
	plain = addStyle("plain", null);
	StyleConstants.setForeground(plain, Color.BLACK);
	StyleConstants.setBold(plain, false);

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
		// nothing
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
	blocks = new ArrayList<StyleToken>();
	String sourceCode = getText();
	int sourceLength = sourceCode.length();
	for (String target : JAVA_KEYWORDS) {
	    int targetLength = target.length();

	    for (int pos = 0; pos + targetLength < sourceLength; pos++) {
		if (pos > 0 && pos + targetLength + 1 < sourceLength) {
		    if (!Character.isAlphabetic(sourceCode.charAt(pos - 1))
			    && !Character.isAlphabetic(sourceCode.charAt(pos
				    + targetLength))) {
			edgesOkay(sourceCode, pos, target);
		    }
		} else if (pos == 0 && pos + targetLength + 1 < sourceLength) {
		    if (!Character.isAlphabetic(sourceCode.charAt(pos
			    + targetLength)))
			edgesOkay(sourceCode, pos, target);
		} else if (pos > 0 && pos + targetLength + 1 == sourceLength) {
		    if (!Character.isAlphabetic(sourceCode.charAt(pos - 1)))
			edgesOkay(sourceCode, pos, target);
		} else if (pos == 0 && pos + targetLength + 1 == sourceLength)
		    edgesOkay(sourceCode, pos, target);
	    }
	}

	for (StyleToken styleToken : blocks) {
	    SwingUtilities.invokeLater(new HighlightKeywordsLater(styleToken
		    .getPos(), styleToken.getLength()));
	}
    }

    // private int nextNonLetterIdx(String str, int startIdx) {
    // for (int i = startIdx; i < str.length(); i++) {
    // if (!Character.isAlphabetic(str.charAt(i)))
    // return i;
    // }
    // return -1;
    // }

    private boolean arrayContains(String[] array, String target) {
	for (String string : array) {
	    if (string.equals(target))
		return true;
	}
	return false;
    }

    private void edgesOkay(String sourceCode, int pos, String target) {
	String candidate = sourceCode.substring(pos, pos + target.length());
	if (arrayContains(JAVA_KEYWORDS, candidate))
	    blocks.add(new StyleToken(candidate.length(), pos));
    }

    private class HighlightKeywordsLater implements Runnable {

	private int pos;
	private int nextToken;

	private HighlightKeywordsLater(int pos, int nextToken) {
	    this.pos = pos;
	    this.nextToken = nextToken;
	}

	@Override
	public void run() {
	    doc.setCharacterAttributes(pos, nextToken, keywords, false);
	}

    }

    private class RevertToPlainLater implements Runnable {

	private int pos;
	private String nextToken;

	private RevertToPlainLater(int pos, String nextToken) {
	    this.pos = pos;
	    this.nextToken = nextToken;
	}

	@Override
	public void run() {
	    doc.setCharacterAttributes(pos, nextToken.length(), plain, false);
	}

    }
}