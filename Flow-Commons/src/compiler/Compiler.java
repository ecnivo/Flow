package compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Logger;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import struct.TextDocument;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Represents a wrapper around the javac compiler
 * Attempts to compile a document, then execute
 * <p>
 * Created by Netdex on 12/18/2015.
 * <p>
 * TODO FIX COMPILER TO USE NEW FILE STRUCTURE
 */
public class Compiler {

    private TextDocument[] textDocuments;
    private UUID dirUUID;
    private File workingDirectory;
    private static final Logger L = Logger.getLogger("Flow-Commons/Compiler");

    /**
     * Instantiates a compiler from the given textDocuments
     *
     * @param doc The textDocuments to compile
     */
    public Compiler(TextDocument... doc) {
        this.textDocuments = doc;
        this.dirUUID = UUID.randomUUID();
        this.workingDirectory = new File(System.getenv("APPDATA") + File.separator + "flow" + File.separator + dirUUID.toString());
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s [%1$tc]%n");

        throw new NotImplementedException();
    }

    /**
     * Attempts to build a set of textDocuments into classes
     *
     * @return The diagnostics containing compilation errors and warnings
     * @throws IOException when files cannot be written
     */
    public List<Diagnostic<? extends JavaFileObject>> build() throws IOException {
        L.info("Found working directory of " + workingDirectory.getAbsolutePath());
        ArrayList<File> paths = new ArrayList<>();
        if (!workingDirectory.exists()) {
            workingDirectory.mkdir();
            L.info("Working directory did not exist, created it");
        }
        L.info(textDocuments.length + " textDocuments queued for compilation");
        for (TextDocument doc : textDocuments) {
            if (doc.getParentFile() == null)
                throw new IllegalArgumentException("Compiler contains a text document without link to file reference!");

            File tempPath = new File(workingDirectory.getAbsolutePath() + File.separator + doc.getParentFile().getParentDirectory().getFullyQualifiedPath() + File.separator + doc.getParentFile().getFileName());
            if (tempPath.getParentFile().mkdirs()) {
                L.info("Directory " + tempPath.getParent() + " did not exist, created!");
            }
            paths.add(tempPath);
            PrintStream ps = new PrintStream(tempPath);
            ps.println(doc.getDocumentText());
            ps.close();
            L.info("Wrote " + doc.getParentFile().getFileName() + " to temporary path of " + tempPath.getAbsolutePath());
        }

        try {
            L.info("Setting up compilation diagnostics and compiler");
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

            L.info("Setting up cmd arguments for compiler");
            List<String> optionList = new ArrayList<>();
            optionList.add("-g"); // FOR THE DEBUGGER
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

    public Process execute() throws IOException {
        /* TODO actually fix this, instead of commenting it out to remove the error
        String remotePath = (textDocuments[0].getParentFile().getRemotePath() == "" ? "" : textDocuments[0].getParentFile().getRemotePath() + "/") // YOU MUST USE A FORWARD SLASH HERE OR ELSE IT WON'T WORK
                + removeExtension(textDocuments[0].getParentFile().getRemoteName());
        L.info("Assuming main class is " + remotePath);
        ProcessBuilder pb = new ProcessBuilder("java", "-cp", workingDirectory.getAbsolutePath(), remotePath);
        L.info("Execution arguments are: " + pb.command().toString());
        Process p = pb.start();
        return p;*/
        return null;
    }

    public String readAllOutput() throws IOException {
        L.info("Reading all output of execution");
        String str = "";
        Process p = this.execute();
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = br.readLine()) != null) {
            L.info(line);
            str += line + '\n';
        }
        return str;
    }

    protected File getWorkingDirectory() {
        return workingDirectory;
    }

    protected TextDocument[] getFlowFiles() {
        return textDocuments;
    }

    protected static String removeExtension(String s) {
        String separator = System.getProperty("file.separator");
        String filename;
        int lastSeparatorIndex = s.lastIndexOf(separator);
        if (lastSeparatorIndex == -1) {
            filename = s;
        } else {
            filename = s.substring(lastSeparatorIndex + 1);
        }
        int extensionIndex = filename.lastIndexOf(".");
        if (extensionIndex == -1)
            return filename;
        return filename.substring(0, extensionIndex);
    }
}

