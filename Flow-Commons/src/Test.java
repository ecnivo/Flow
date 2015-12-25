import compiler.Compiler;
import struct.TextDocument;

import java.io.IOException;

/**
 * Created by Netdex on 12/18/2015.
 */
public class Test {
    public static void main(String[] args) throws IOException {
        TextDocument doc = new TextDocument("test", "Test.java");
        doc.setDocumentText("package test;public class Test { public static void main(String[] args){System.out.println(\"hello world!\");}}");
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
