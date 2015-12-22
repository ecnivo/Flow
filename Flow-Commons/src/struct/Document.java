package struct;

import message.DocumentDeleteMessage;
import message.DocumentInsertMessage;
import message.DocumentMessage;
import network.MalformedParcelableException;
import network.Parcelable;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Represents a editable text document
 * Created by Netdex on 12/18/2015.
 */
public class Document implements Parcelable {

    private String id;
    private ArrayList<String> lines;

    public Document(String id) {
        this.id = id;
        this.lines = new ArrayList<>();
        lines.add("");
    }

    public String getID() {
        return id;
    }

    /**
     * Insert a character at line number at index
     *
     * @param c          The character to add
     * @param lineNumber The line number to add the character
     * @param idx        The index to add the character at
     * @return whether or not line count was affected by this operation
     */
    public boolean insert(char c, int lineNumber, int idx) {
        if (lineNumber >= lines.size() || lineNumber < 0)
            throw new ArrayIndexOutOfBoundsException("Line number is out of range");
        String line = lines.get(lineNumber);
        if (idx < 0 || idx > line.length())
            throw new ArrayIndexOutOfBoundsException("Index in line is out of range");
        if (c == '\n') {
            String oldLine = line.substring(0, idx);
            String newLine = line.substring(idx);
            lines.set(lineNumber, oldLine);
            lines.add(lineNumber + 1, newLine);
            return true;
        } else {
            line = line.substring(0, idx) + c + line.substring(idx);
            lines.set(lineNumber, line);
            return false;
        }
    }

    /**
     * Remove a character at line number at index
     *
     * @param lineNumber The line number to delete the character
     * @param idx        The index of the character to delete, -1 to remove the line
     * @return whether or not line count was affected by this operation
     */
    public boolean delete(int lineNumber, int idx) {
        if (lineNumber >= lines.size() || lineNumber < 0)
            throw new ArrayIndexOutOfBoundsException("Line number is out of range");
        String line = lines.get(lineNumber);
        if (idx == -1) {
            lines.remove(lineNumber);
            return true;
        } else {
            if (idx < 0 || idx >= line.length())
                throw new ArrayIndexOutOfBoundsException("Index in line is out of range");
            line = line.substring(0, idx) + line.substring(idx + 1);
            lines.set(lineNumber, line);
            return false;
        }
    }

    /**
     * Get all the lines in the document as a string
     *
     * @return All the lines in the document as a string
     */
    public String getDocumentText() {
        String str = "";
        for (String s : lines)
            str += s + '\n';
        return str;
    }

    /**
     * Sets the text of the document to a string
     *
     * @param str The string to set the text of the document to
     */
    public void setDocumentText(String str) {
        lines.clear();
        lines.add("");
        int lineIdx = 0;
        int idx = 0;
        for (char c : str.toCharArray()) {
            if (insert(c, lineIdx, idx++)) {
                lineIdx++;
                idx = 0;
            }
        }
    }

    /**
     * Gets a line in a document
     *
     * @param lineNumber The line number of the line to get
     * @return The line at that line number
     */
    public String getLine(int lineNumber) {
        return lines.get(lineNumber);
    }

    public void executeMessage(DocumentMessage message) {
        DocumentMessage.DocumentMessageType type = message.getDocumentMessageType();
        if (type == DocumentMessage.DocumentMessageType.CHARACTER_INSERT) {
            DocumentInsertMessage dim = (DocumentInsertMessage) message;
            this.insert(dim.getCharacter(), dim.getLineNumber(), dim.getIndex());
        } else if (type == DocumentMessage.DocumentMessageType.CHARACTER_DELETE) {
            DocumentDeleteMessage dem = (DocumentDeleteMessage) message;
            this.delete(dem.getLineNumber(), dem.getIndex());
        }
    }

    @Override
    public byte[] serialize() {
        byte[] idBuffer = id.getBytes();
        byte[][] lineBuffer = new byte[lines.size()][];
        int size = idBuffer.length + 4 + 4;
        for (int i = 0; i < lines.size(); i++) {
            lineBuffer[i] = lines.get(i).getBytes();
            size += lineBuffer[i].length + 4;
        }

        // Allocate a reasonably sized buffer
        ByteBuffer buffer = ByteBuffer.allocate(size);

        // Add the size of the ID and the ID itself into the buffer
        buffer.putInt(idBuffer.length);
        buffer.put(idBuffer);

        // Add the number of lines into the buffer
        buffer.putInt(lines.size());

        // Add the size of each line and the lines into the buffer
        for (int i = 0; i < lineBuffer.length; i++) {
            buffer.putInt(lineBuffer[i].length);
            buffer.put(lineBuffer[i]);
        }
        return buffer.array();
    }

    @Override
    public byte[] deserialize(byte[] data) throws MalformedParcelableException {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(data);

            // Read in the ID
            int idBufferSize = buffer.getInt();
            byte[] idBuffer = new byte[idBufferSize];
            buffer.get(idBuffer);
            this.id = new String(idBuffer);

            // Read in all the lines
            int lineCount = buffer.getInt();
            this.lines.clear();
            for (int i = 0; i < lineCount; i++) {
                int lineLength = buffer.getInt();
                byte[] lineBuffer = new byte[lineLength];
                buffer.get(lineBuffer);
                this.lines.add(new String(lineBuffer));
            }

            byte[] remaining = new byte[buffer.remaining()];
            buffer.get(remaining);
            return remaining;
        } catch (Exception e) {
            throw new MalformedParcelableException();
        }
    }
}
