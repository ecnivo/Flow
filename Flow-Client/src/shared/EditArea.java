package shared;

import callback.DocumentCallbackEvent;
import callback.TextModificationListener;
import editing.UserCaret;
import gui.FlowClient;
import message.Data;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.UUID;

@SuppressWarnings("serial")
public class EditArea extends JTextPane {
    private JScrollPane scrolling;
    private StyledDocument doc;
    private UUID versionTextUUID;
    private UUID fileUUID;
    private UUID projectUUID;

    private Style keywordStyle;
    private Style plainStyle;
    private Style stringStyle;
    private Style commentsStyle;

    private ArrayList<StyleToken> keywordBlocks;
    private ArrayList<StyleToken> stringBlocks;
    private ArrayList<StyleToken> commentBlocks;

    public static final Font PLAIN = new Font("Consolas", Font.PLAIN, 13);
    public static final Color PLAIN_COLOUR = Color.BLACK;
    public static final Color KEYWORD_COLOUR = new Color(0x38761D);
    public static final Color STRING_COLOUR = new Color(0xA30BCF);
    public static final Color COMMENTS_COLOUR = new Color(0xD13313);

    private static final String[] JAVA_KEYWORDS = {"abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throws", "throw", "transient", "try", "void", "volatile", "while"};

    private boolean ignoreEvents = false;

    public EditArea(String textDoc, UUID projectUUID, UUID fileUUID, UUID versionTextUUID, boolean editable, EditTabs tabs) {
        setLayout(null);
        this.projectUUID = projectUUID;
        this.versionTextUUID = versionTextUUID;
        this.fileUUID = fileUUID;
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
        editorListRequest.put("session_id", Communicator.getSessionID());
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
                if (ignoreEvents)
                    return;
                String insertedString = "";
                int strLen = e.getLength();
                int vinceYoureRetardedYouShouldHaveUsedThisInsteadOmgVince = e.getOffset();
                try {
                    insertedString = doc.getText(vinceYoureRetardedYouShouldHaveUsedThisInsteadOmgVince, strLen);
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
                Data fileModify = new Data("file_text_modify");
                fileModify.put("file_uuid", fileUUID);
                fileModify.put("session_id", Communicator.getSessionID());
                fileModify.put("mod_type", "INSERT");

                System.out.println("inserting " + insertedString + " at " + vinceYoureRetardedYouShouldHaveUsedThisInsteadOmgVince);

                fileModify.put("idx", vinceYoureRetardedYouShouldHaveUsedThisInsteadOmgVince);
                fileModify.put("str", insertedString);

                Data response = Communicator.communicate(fileModify);
                String status = response.get("status", String.class);
                if (!status.equals("OK")) {
                    return;
                }

                highlightSyntax();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (ignoreEvents)
                    return;
                int removedLen = e.getLength();
                int vinceYoureRetardedYouShouldHaveUsedThisInsteadOmgVince = e.getOffset();

                Data metadataModify = new Data("file_text_modify");
                metadataModify.put("file_uuid", fileUUID);
                metadataModify.put("session_id", Communicator.getSessionID());
                metadataModify.put("mod_type", "DELETE");

                System.out.println("removing length " + removedLen + " from position " + vinceYoureRetardedYouShouldHaveUsedThisInsteadOmgVince);

                metadataModify.put("idx", vinceYoureRetardedYouShouldHaveUsedThisInsteadOmgVince);
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
            public void onDocumentUpdate(DocumentCallbackEvent e) {
                if (e.USERNAME.equals(Communicator.getUsername())) {
                    return;
                }

                if (e.TYPE == DocumentCallbackEvent.DocumentCallbackType.INSERT) {
                    String addition = e.ADDITION;
                    try {
                        ignoreEvents = true;
                        doc.insertString(e.INDEX, addition, null);
                        ignoreEvents = false;
                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
                    }
                } else if (e.TYPE == DocumentCallbackEvent.DocumentCallbackType.DELETE) {
                    int length = e.REMOVAL_LENGTH;
                    try {
                        ignoreEvents = true;
                        doc.remove(e.INDEX, length);
                        ignoreEvents = false;
                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
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

    public UUID getFileUUID() {
        return fileUUID;
    }

    private class StyleToken {
        private int length;
        private int premiereIdx;

        private StyleToken(int length, int firstIdx) {
            this.length = length;
            this.premiereIdx = firstIdx;
        }

        private int getLength() {
            return length;
        }

        private int getFirstIdx() {
            return premiereIdx;
        }
    }

    private void highlightSyntax() {
        keywordBlocks = new ArrayList<StyleToken>();
        stringBlocks = new ArrayList<StyleToken>();
        commentBlocks = new ArrayList<StyleToken>();

        String sourceCode = getText();
        int sourceLength = sourceCode.length();
        int lines = 0;
        for (int pos = 0; pos < sourceLength; pos++) {
            if (!Character.isAlphabetic(sourceCode.charAt(pos)) || pos == 0) {
                int end = nextNonAlphabetic(sourceCode, pos);
                if (end >= 0 || (pos == 0 && end == -1)) {
                    String candidate = sourceCode.substring(pos, end);
                    if (arrayContains(JAVA_KEYWORDS, candidate.trim())) {
                        keywordBlocks.add(new StyleToken(end - pos, pos - lines));
                        pos = end - 1;
                    }
                } else {
                    break;
                }
            }
            if (sourceCode.charAt(pos) == '\n') {
                lines++;
            }
        }

        lines = 0;
        for (int pos = 0; pos < sourceLength; pos++) {
            if (sourceCode.charAt(pos) == '"') {
                boolean escaped;
                if ((pos > 0 && sourceCode.charAt(pos - 1) != '\\') || pos == 0) {
                    escaped = false;
                } else {
                    escaped = true;
                    continue;
                }
                if (!escaped) {
                    int closeQuote = sourceCode.indexOf('"', pos + 1);

                    while (closeQuote > 0 && sourceCode.charAt(closeQuote - 1) == '\\') {
                        closeQuote = sourceCode.indexOf('"', closeQuote + 1);
                    }
                    if (closeQuote < 0) {
                        continue;
                    }
                    stringBlocks.add(new StyleToken(closeQuote + 1 - pos + lines, pos - lines));
                    pos = closeQuote;
                }
            } else if (sourceCode.charAt(pos) == '\'') {
                boolean escaped;
                if ((pos > 0 && sourceCode.charAt(pos - 1) != '\\') || pos == 0) {
                    escaped = false;
                } else {
                    escaped = true;
                    continue;
                }
                if (!escaped) {
                    int closeQuote = sourceCode.indexOf('\'', pos + 1);
                    while (closeQuote > 0 && sourceCode.charAt(closeQuote - 1) == '\\') {
                        closeQuote = sourceCode.indexOf('\'', closeQuote + 1);
                    }
                    if (closeQuote < 0) {
                        continue;
                    }
                    stringBlocks.add(new StyleToken(closeQuote + 1 - pos + lines, pos - lines));
                    pos = closeQuote;
                }
            } else if (sourceCode.charAt(pos) == '\n') {
                lines++;
            }
        }

        lines = 0;
        for (int pos = 0; pos < sourceLength - 1; pos++) {
            if (sourceCode.substring(pos, pos + 1).equals("////")) {
                int nextLine = sourceCode.indexOf('\n', pos);
                if (nextLine == -1)
                    nextLine = sourceLength - 1;
                commentBlocks.add(new StyleToken(nextLine - pos, pos - lines));
                pos = nextLine;
            } else if (sourceCode.substring(pos, pos + 1).equals("//*")) {
                int end = sourceCode.indexOf("*//", pos);
                if (end < 0) {
                    continue;
                }
                commentBlocks.add(new StyleToken(end + 2 - pos, pos - lines));
                pos = end + 2;
            } else if (sourceCode.charAt(pos) == '\n') {
                lines++;
            }
        }

        SwingUtilities.invokeLater(new FormatPlainLater(0, sourceLength));

        for (StyleToken styleToken : keywordBlocks) {
            SwingUtilities.invokeLater(new FormatKeywordsLater(styleToken.getFirstIdx(), styleToken.getLength()));
        }
        for (StyleToken styleToken : stringBlocks) {
            SwingUtilities.invokeLater(new FormatStringsLater(styleToken.getFirstIdx(), styleToken.getLength() + 1));
        }
        for (StyleToken styleToken : commentBlocks) {
            SwingUtilities.invokeLater(new FormatCommentsLater(styleToken.getFirstIdx(), styleToken.getLength()));
        }
    }

    private boolean arrayContains(String[] array, String target) {
        for (String string : array) {
            if (string.equals(target))
                return true;
        }
        return false;
    }

    private int nextNonAlphabetic(String sourceCode, int idx) {
        int sourceCodeLength = sourceCode.length();
        do {
            idx++;
        } while (idx < sourceCodeLength - 1 && Character.isAlphabetic(sourceCode.charAt(idx)));
        return idx;
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
            doc.setCharacterAttributes(pos, nextToken, keywordStyle, true);
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
            doc.setCharacterAttributes(pos, nextToken, plainStyle, true);
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
            doc.setCharacterAttributes(pos, nextToken, stringStyle, true);
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
            doc.setCharacterAttributes(pos, nextToken, commentsStyle, true);
        }
    }
}