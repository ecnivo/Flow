package compiler;

import struct.VersionText;

import java.nio.file.Paths;

/**
 * Represents a text document with information pertaining to compilation
 * Created by Gordon Guan on 1/17/2016.
 */
public class CompilableText extends VersionText {
    private String path;
    private String name;

    public CompilableText(String text, String path, String name) {
        super(text);
        this.path = path;
        this.name = name;
    }

    /**
     * The path to the file
     *
     * @return
     */
    public String getPath() {
        return path;
    }

    /**
     * The name of the file
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the full path of the file
     * @return
     */
    public String getFullPath() {
        return Paths.get(path, name).toString();
    }
}
