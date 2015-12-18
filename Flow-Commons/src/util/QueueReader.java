package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;

/**
 * A version of a BufferedReader which allows peek() operations to look at the next line without advancing
 * Created by Netdex on 12/17/2015.
 */
public class QueueReader {

    private InputStream is;
    private Queue<Integer> pushback = new LinkedList<Integer>();

    public QueueReader(InputStream in) {
        this.is = in;
    }


    public byte[] peek(int len) {
        try {
            byte[] buf = new byte[len];
            is.read(buf);
            return buf;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}
