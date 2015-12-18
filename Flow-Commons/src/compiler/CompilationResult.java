package compiler;

/**
 * Represents the result of the compilation process
 * Created by Netdex on 12/18/2015.
 */
public class CompilationResult {

    protected CompilationResult(){

    }

    static class CompilerWarning {

    }

    static class CompilerError {

    }

    static enum CompilationStatus {
        FAILURE,
        WARNING,
        NONE
    }
}
