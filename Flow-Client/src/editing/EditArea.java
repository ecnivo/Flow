package editing;

import gui.FlowClient;

import java.awt.Color;
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
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class EditArea extends JTextPane {
    private JScrollPane scrolling;
    private StyledDocument doc;
    private Style keywords;
    private Style plain;
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
	doc = (StyledDocument) getDocument();
	setEditable(editable);

	keywords = addStyle("keywords", null);
	StyleConstants.setForeground(keywords, new Color(255, 171, 0));
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
	private String token;
	private int pos;

	private StyleToken(String token, int pos) {
	    this.token = token;
	    this.pos = pos;
	}

	private String getToken() {
	    return token;
	}

	private int getPos() {
	    return pos;
	}
    }

    private void highlightSyntax() {
	String words = getText().trim();
	ArrayList<StyleToken> blocks = new ArrayList<StyleToken>();
	int pos = 0;
	int nextWhitespace = indexOfWhitespace(words, pos);
	while (nextWhitespace > -1 && pos < words.length()) {
	    String nextToken = words.substring(pos, nextWhitespace);
	    if (arrayContains(JAVA_KEYWORDS, nextToken.trim())) {
		SwingUtilities.invokeLater(new SetStyleLater(pos, nextToken));
	    }
	    blocks.add(new StyleToken(nextToken.trim(), pos));
	    pos = nextWhitespace;
	    nextWhitespace = indexOfWhitespace(words, pos+1);
	}
	System.out.println("quit loop");
	blocks.add(new StyleToken(words.substring(pos).trim(), pos));
    }

    private int indexOfWhitespace(String str, int startVal) {
	for (int i = startVal; i < str.length(); i++) {
	    if (Character.isWhitespace(str.charAt(i)))
		return i;
	}
	return -1;
    }

    private boolean arrayContains(Object[] array, Object target) {
	for (Object object : array) {
	    if (object.equals(target))
		return true;
	}
	return false;
    }

    private class SetStyleLater implements Runnable {

	private int pos;
	private String nextToken;

	private SetStyleLater(int pos, String nextToken) {
	    this.pos = pos;
	    this.nextToken = nextToken;
	}

	@Override
	public void run() {
	    doc.setCharacterAttributes(pos, nextToken.length(), keywords, false);
	}

    }
}