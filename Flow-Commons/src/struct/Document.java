package struct;

import network.CorruptedParcelableException;
import network.Parcelable;

import java.nio.Buffer;
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
    }

    /**
     * Insert a character at line number at index
     *
     * @param c          The character to add
     * @param lineNumber The line number to add the character
     * @param idx        The index to add the character at
     */
    public void insert(char c, int lineNumber, int idx) {
        if (lineNumber >= lines.size() || lineNumber < 0)
            throw new ArrayIndexOutOfBoundsException("Line number is out of range");
        String line = lines.get(lineNumber);
        if (idx < 0 || idx >= line.length())
            throw new ArrayIndexOutOfBoundsException("Index in line is out of range");
        if (c == '\n') {
            String oldLine = line.substring(0, idx);
            String newLine = line.substring(idx);
            lines.set(lineNumber, oldLine);
            lines.add(lineNumber + 1, newLine);
        } else
            line = line.substring(0, idx) + c + line.substring(idx);
        lines.set(lineNumber, line);
    }

    /**
     * Remove a character at line number at index
     *
     * @param lineNumber The line number to delete the character
     * @param idx        The index of the character to delete
     */
    public void delete(int lineNumber, int idx) {
        if (lineNumber >= lines.size() || lineNumber < 0)
            throw new ArrayIndexOutOfBoundsException("Line number is out of range");
        String line = lines.get(lineNumber);
        if (idx < 0 || idx >= line.length())
            throw new ArrayIndexOutOfBoundsException("Index in line is out of range");
        line = line.substring(0, idx) + line.substring(idx + 1);
        lines.set(lineNumber, line);
    }

    /**
     * Get all the lines in the document as a string
     *
     * @return All the lines in the document as a string
     */
    public String getDocumentText() {
        return lines.toString();
    }

    @Override
    public byte[] serialize() {
        // Allocate a reasonably sized buffer
        ByteBuffer buffer = ByteBuffer.allocate(65535);

        // Add the size of the ID and the ID itself into the buffer
        byte[] idBuffer = id.getBytes();
        buffer.putInt(idBuffer.length);
        buffer.put(idBuffer);

        // Add the number of lines into the buffer
        buffer.putInt(lines.size());

        // Add the size of each line and the lines into the buffer
        byte[][] lineBuffer = new byte[lines.size()][];
        for (int i = 0; i < lines.size(); i++)
            lineBuffer[i] = lines.get(i).getBytes();
        for (int i = 0; i < lineBuffer.length; i++) {
            buffer.putInt(lineBuffer[i].length);
            buffer.put(lineBuffer[i]);
        }
        return buffer.array();
    }

    @Override
    public Parcelable deserialize(byte[] data) throws CorruptedParcelableException {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(data);
            int idx = 0;

            // Read in the ID
            int idBufferSize = buffer.get(idx += 4);
            byte[] idBuffer = new byte[idBufferSize];
            buffer.get(idBuffer, idx, idBufferSize);
            idx += idBufferSize;
            this.id = new String(idBuffer);

            // Read in all the lines
            int lineCount = buffer.getInt(idx += 4);
            this.lines.clear();
            for (int i = 0; i < lineCount; i++) {
                int lineLength = buffer.get(idx += 4);
                byte[] lineBuffer = new byte[lineLength];
                buffer.get(lineBuffer, idx, lineLength);
                idx += lineLength;
                this.lines.add(new String(lineBuffer));
            }
            return this;
        } catch (Exception e) {
            throw new CorruptedParcelableException();
        }
    }
}
