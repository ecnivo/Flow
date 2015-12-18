package compiler;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents standard input and output of a program
 * Created by Netdex on 12/18/2015.
 */
public class StandardPut {
    private InputStream stdIn;
    private OutputStream stdOut;

    protected StandardPut(InputStream in, OutputStream out){
        this.stdIn = in;
        this.stdOut = out;
    }

    public InputStream getStdIn() {
        return stdIn;
    }

    public OutputStream getStdOut() {
        return stdOut;
    }
}
