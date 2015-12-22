import compiler.Compiler;
import message.DocumentInsertMessage;
import struct.Document;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Netdex on 12/18/2015.
 */
public class Test {
    public static void main(String[] args) throws IOException {
        messageTest();
    }

    public static void messageTest() {
        DocumentInsertMessage dim = new DocumentInsertMessage('c', 1, 1);
        byte[] serial = dim.serialize();
        System.out.println(Arrays.toString(serial));
        System.out.println(bytesToHex(serial));
    }

    public static void compilerTest() throws IOException {
        Document d = new Document("Test.java");
        d.setDocumentText(
                "public class Test {\n" +
                        "   public static void main(String[] args){\n" +
                        "System.out.println(\"Hello world!\")\n" +
                        "   }\n" +
                        "}\n");
        Compiler c = new Compiler(d);
        c.build();
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
