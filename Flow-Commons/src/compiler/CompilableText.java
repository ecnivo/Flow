package compiler;

import struct.VersionText;

import java.nio.file.Paths;

/**
 * Created by Netdex on 1/17/2016.
 */
public class CompilableText extends VersionText {
    private String path;
    private String name;

    public CompilableText(String text, String path, String name) {
        super(text);
        this.path = path;
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public String getFullPath() {
        return Paths.get(path, name).toString();
    }
}
