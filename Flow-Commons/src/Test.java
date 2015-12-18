import struct.Document;

/**
 * Created by Netdex on 12/18/2015.
 */
public class Test {
    public static void main(String[] args) {
        Document d = new Document("TRASH");
        d.insert('A', 0, 0);
        d.insert('B', 0, 1);
        d.insert('C', 0, 0);
        d.insert('D', 0, 1);
        d.insert('E', 0, 0);
        d.insert('F', 0, 1);
        d.insert('\n', 0, 3);
        d.insert('\n', 1, 2);
        System.out.println(d.getDocumentText());
        System.out.println(d.getLine(1));
    }
}
