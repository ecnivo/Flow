package compiler;

import struct.VersionText;

import java.nio.file.Paths;
import java.util.UUID;

/**
 * Represents a text document with information pertaining to compilation
 * Created by Gordon Guan on 1/17/2016.
 */
public class CompilableText extends VersionText {
    private final UUID fileUUID;
    private final String path;
    private final String name;

    public CompilableText(String text, UUID fileUUID, String path, String name) {
        super(text);
        this.path = path;
        this.name = name;
        this.fileUUID = fileUUID;
    }

    /**
     * The path to the file
     *
     * @return The path to the file
     */
    public String getPath() {
        return path;
    }

    /**
     * The name of the file
     * @return the name of the file
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the full path of the file
     * @return the full path of the file
     */
    public String getFullPath() {
        return Paths.get(path, name).toString();
    }

    /**
     * Get the uuid this file is associated with
     *
     * @return the uuid this file is associated with
     */
    public UUID getFileUUID() {
        return fileUUID;
    }
}
