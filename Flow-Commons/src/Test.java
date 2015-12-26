import compiler.Compiler;
import struct.FlowFile;
import struct.TextDocument;
import util.DiffMatchPatch;

import java.io.IOException;
import java.util.Date;

/**
 * Created by Netdex on 12/18/2015.
 */
public class Test {
    public static void main(String[] args) throws IOException {
        DiffMatchPatch dif = new DiffMatchPatch();

        FlowFile flowFile = new FlowFile("", "Test.java");
        TextDocument doc = new TextDocument(flowFile);
        doc.setDocumentText("public class Test { public static void main(String[] args){System.out.println(\"hello world!\");}}");
        flowFile.addVersion(new Date(), doc);
        Compiler compiler = new Compiler(doc);
        compiler.build();
        compiler.readAllOutput();
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
