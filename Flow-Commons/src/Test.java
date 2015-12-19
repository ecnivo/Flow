import struct.Document;
import compiler.Compiler;

import java.io.IOException;

/**
 * Created by Netdex on 12/18/2015.
 */
public class Test {
    public static void main(String[] args) throws IOException {
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
}
