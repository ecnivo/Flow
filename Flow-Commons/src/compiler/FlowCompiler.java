
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

import struct.VersionText;

/**
 * Represents a wrapper around the javac compiler Attempts to compile a
 * document, then execute
 * <p>
 * Created by Netdex on 12/18/2015.
 * <p>
 * TODO FIX COMPILER TO USE NEW FILE STRUCTURE
 */
public class FlowCompiler {

	private static final Logger	L	= Logger.getLogger("Flow-Commons/Compiler");
	private CompilableText[]	versionTexts;
	private UUID				dirUUID;
	private File				workingDirectory;

	/**
	 * Instantiates a compiler from the given textDocuments
	 *
	 * @param doc
	 *        The textDocuments to compile
	 */
	public FlowCompiler(CompilableText... doc) {
		this.versionTexts = doc;
		this.dirUUID = UUID.randomUUID();
		this.workingDirectory = new File("flow" + File.separator + dirUUID.toString());
		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s [%1$tc]%n");
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

	/**
	 * Attempts to build a set of textDocuments into classes
	 *
	 * @return The diagnostics containing compilation errors and warnings
	 * @throws IOException
	 *         when files cannot be written
	 */
	public List<Diagnostic<? extends JavaFileObject>> build() throws IOException, NoJDKFoundException {
		L.info("found working directory of " + workingDirectory.getAbsolutePath());
		ArrayList<File> paths = new ArrayList<>();
		if (!workingDirectory.exists()) {
			workingDirectory.mkdir();
			L.info("working directory did not exist, created it");
		}
		L.info(versionTexts.length + " textdocuments queued for compilation");
		for (CompilableText doc : versionTexts) {
			File tempPath = new File(workingDirectory.getAbsolutePath(), doc.getFullPath());
			if (tempPath.getParentFile().mkdirs()) {
				L.info("directory " + tempPath.getParent() + " did not exist, created!");
			}
			paths.add(tempPath);
			PrintStream ps = new PrintStream(tempPath);
			ps.println(doc.getDocumentText());
			ps.close();
			L.info("wrote " + doc.getName() + " to temporary path of " + tempPath.getAbsolutePath());
		}
		System.out.println(paths);
		try {
			L.info("setting up compilation diagnostics and compiler");
			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			if (compiler == null) {
				L.severe("You do not have compatible JDK installed to compile your code.");
				throw new NoJDKFoundException();
			}
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

			L.info("setting up cmd arguments for compiler");
			List<String> optionList = new ArrayList<>();
			optionList.add("-g"); // FOR THE DEBUGGER
			optionList.add("-classpath");
			optionList.add(workingDirectory.getAbsolutePath());

			L.info("setting up compilation task");
			Iterable<? extends JavaFileObject> compilationUnit = fileManager.getJavaFileObjectsFromFiles(paths);
			JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, optionList, null, compilationUnit);

			L.info("calling compilation task");
			if (task.call()) {
				L.info("compilation success!");
			} else {
				L.severe("failed to compile!");
				for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
					L.severe(String.format("error on line %d in %s: %s", diagnostic.getLineNumber(), diagnostic.getSource().toUri(), diagnostic.getMessage(Locale.getDefault())));
				}
				return diagnostics.getDiagnostics();
			}
			fileManager.close();
		} catch (Exception e) {
			L.severe("exception occurred while compiling!");
			L.severe(e.getMessage());
		}
		return null;
	}

	public class NoJDKFoundException extends Exception {

		public NoJDKFoundException() {
		}
	}

	public Process execute() throws IOException {
		String remotePath = (versionTexts[0].getPath() == "" ? "" : versionTexts[0].getPath() + "/")
				+ removeExtension(versionTexts[0].getFullPath());
		// YOU MUST USE A FORWARD SLASH OR ELSE IT WON'T WORK
		L.info("assuming main class is " + remotePath);
		ProcessBuilder pb = new ProcessBuilder("java", "-cp", "\"" + workingDirectory.getAbsolutePath() + "\"", remotePath);
		L.info("execution arguments are: " + pb.command().toString());
		Process p = pb.start();
		return p;
	}

	public String readAllOutput() throws IOException {
		L.info("reading all output of execution");
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

	protected VersionText[] getFlowFiles() {
		return versionTexts;
	}
}
