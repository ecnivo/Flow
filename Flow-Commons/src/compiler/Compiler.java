package compiler;

import struct.Document;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Represents a wrapper around the javac compiler
 * Attempts to compile a document, then execute
 *
 * Created by Netdex on 12/18/2015.
 */
public class Compiler {

    private Document[] documents;

    public Compiler(Document... doc){
        this.documents = doc;
    }

    public CompilationResult build() throws IOException {
        for(Document doc : documents){
            String body = doc.getDocumentText();
            FileOutputStream fos  = new FileOutputStream("todo");
        }

        throw new NotImplementedException();
    }

    public StandardPut execute(){
        ProcessBuilder pb = new ProcessBuilder();
        throw new NotImplementedException();
    }
}

