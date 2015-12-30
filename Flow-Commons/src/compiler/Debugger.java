package compiler;

import struct.TextDocument;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;

/**
 * Created by Netdex on 12/25/2015.
 */
public class Debugger {

    private Compiler compiler;
    private File workingDirectory;

    private Process p;
    private BufferedReader in;
    private PrintStream out;

    public Debugger(TextDocument... flowFiles) {
        this.compiler = new Compiler(flowFiles);
        this.workingDirectory = compiler.getWorkingDirectory();
        throw new NotImplementedException(); // FIXME: 12/30/2015
    }

    public Process begin() throws IOException {
        /* TODO actually fix this, instead of commenting it out to remove the error
        ProcessBuilder pb = new ProcessBuilder("jdb", "-classpath", workingDirectory.getAbsolutePath());
        this.p = pb.start();
        this.in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        this.out = new PrintStream(p.getOutputStream());
        out.println("run " + compiler.getFlowFiles()[0].getParentFile().getRemotePath() + File.separator + Compiler.removeExtension(compiler.getFlowFiles()[0].getParentFile().getRemoteName()));
        return p;*/
        return null;
    }

    public void addBreakpoint(String classID, int line) {
        out.println("stop at " + classID + ":" + line);
    }

    public void addBreakpoint(String classID, String methodID) {
        out.println("stop in " + classID + "." + methodID);
    }

    public void removeBreakpoint(String classID, int line) {
        out.println("clear " + classID + ":" + line);
    }

    public void stepIn() {
        out.println("step");
    }

    public void stepOver() {
        out.println("step up");
    }

    public void stepContinue() {
        out.println("cont");
    }
}
