package test;

import org.junit.Test;
import struct.TextDocument;

import static org.junit.Assert.*;

/**
 * Created by Netdex on 12/31/2015.
 */
public class TextDocumentTest {

    @Test
    public void testInsert() throws Exception {
        TextDocument textDocument = new TextDocument();
        textDocument.insert('c', 0, 0);
        assertEquals(textDocument.getDocumentText(), "c\n");
    }

    @Test
    public void testDelete() throws Exception {
        TextDocument textDocument = new TextDocument();
        textDocument.insert('c', 0, 0);
        textDocument.insert('a', 0, 0);
        textDocument.delete(0, 0);
        assertEquals(textDocument.getDocumentText(), "c\n");
    }

    @Test
    public void testGetDocumentText() throws Exception {
        TextDocument textDocument = new TextDocument();
        textDocument.insert('c', 0, 0);
        textDocument.insert('\n', 0, 1);
        textDocument.insert('c', 1, 0);
        textDocument.insert('\n', 1, 1);
        textDocument.insert('c', 2, 0);
        assertEquals(textDocument.getDocumentText(), "c\nc\nc\n");
    }

    @Test
    public void testGetLine() throws Exception {
        TextDocument textDocument = new TextDocument();
        for(int i = 0; i < 10; i++)
            textDocument.insert('c', 0, 0);
        assertEquals(textDocument.getLine(0), "cccccccccc");
    }

    @Test
    public void testSetDocumentText() throws Exception {
        TextDocument textDocument = new TextDocument();
        textDocument.setDocumentText("test\ntest2");
        assertEquals(textDocument.getLine(0), "test");
    }


}