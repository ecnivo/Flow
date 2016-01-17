import compiler.CompilableText;
import compiler.FlowCompiler;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.List;

/**
 * Created by Netdex on 1/17/2016.
 */
public class CommonsTest {
    public static void main(String[] args) throws IOException {
        CompilableText text = new CompilableText("package test;s" +
                                                         "public class test {" +
                                                         "public static void main(String[] args) {" +
                                                         "System.out.println(\"Hello world!\");" +
                                                         "}" +
                                                         "}", "test", "test.java");

        FlowCompiler compiler = new FlowCompiler(text);
        List<Diagnostic<? extends JavaFileObject>> diagnosticList = compiler.build();
        compiler.readAllOutput();
    }
}
