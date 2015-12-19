package compiler;

import struct.Document;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Represents a wrapper around the javac compiler
 * Attempts to compile a document, then execute
 * <p>
 * Created by Netdex on 12/18/2015.
 */
public class Compiler {

    private Document[] documents;
    private static final Logger L = Logger.getLogger("Flow-Commons/Compiler");

    public Compiler(Document... doc) {
        this.documents = doc;
    }

    /**
     * Attempts to build a set of documents into classes
     * @return The diagnostics containing compilation errors and warnings
     * @throws IOException when files cannot be written
     */
    public List<Diagnostic<? extends JavaFileObject>> build() throws IOException {
        File workingDirectory = new File(System.getenv("APPDATA") + File.separator + "flow");
        L.info("Found working directory of " + workingDirectory.getAbsolutePath());
        ArrayList<File> paths = new ArrayList<>();
        if (!workingDirectory.exists()) {
            workingDirectory.mkdir();
            L.info("Working directory did not exist, created it");
        }
        L.info(documents.length + " documents queued for compilation");
        for (Document doc : documents) {
            String body = doc.getDocumentText();
            File tempPath = new File(workingDirectory.getAbsolutePath() + File.separator + doc.getID());
            paths.add(tempPath);
            PrintStream ps = new PrintStream(tempPath);
            ps.println(doc.getDocumentText());
            L.info("Wrote " + doc.getID() + " to temporary path of " + tempPath.getAbsolutePath());
        }

        try {
            L.info("Setting up compilation diagnostics and compiler");
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

            L.info("Setting up cmd arguments for compiler");
            List<String> optionList = new ArrayList<>();
            optionList.add("-classpath");
            optionList.add(workingDirectory.getAbsolutePath());

            L.info("Setting up compilation task");
            Iterable<? extends JavaFileObject> compilationUnit = fileManager.getJavaFileObjectsFromFiles(paths);
            JavaCompiler.CompilationTask task = compiler.getTask(
                    null,
                    fileManager,
                    diagnostics,
                    optionList,
                    null,
                    compilationUnit);

            L.info("Calling compilation task");
            if (task.call()) {
                L.info("Compilation success!");
            } else {
                L.severe("Failed to compile!");
                for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                    L.severe(String.format("Error on line %d in %s: %s", diagnostic.getLineNumber(), diagnostic.getSource().toUri(), diagnostic.getMessage(Locale.getDefault())));
                }
                return diagnostics.getDiagnostics();
            }
            fileManager.close();
        } catch (Exception e) {
            L.severe("Exception occurred while compiling!");
            L.severe(e.getMessage());
        }
        return null;
    }

    public StandardPut execute() {
        ProcessBuilder pb = new ProcessBuilder();
        throw new NotImplementedException();
    }
}

