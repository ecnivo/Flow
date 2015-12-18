package struct;

import network.Parcelable;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by Netdex on 12/18/2015.
 */
public class Document implements Parcelable {

    private String id;
    private ArrayList<String> lines;

    public Document(String id) {
        this.id = id;
        this.lines = new ArrayList<>();
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
    public Parcelable deserialize(byte[] data) {
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
        for(int i = 0; i < lineCount; i++){
            int lineLength = buffer.get(idx += 4);
            byte[] lineBuffer = new byte[lineLength];
            buffer.get(lineBuffer, idx, lineLength);
            idx += lineLength;
            this.lines.add(new String(lineBuffer));
        }
        return this;
    }
}
