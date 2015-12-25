package struct;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Represents a file of arbitrary extension
 * <p>
 * Created by Netdex on 12/24/2015.
 */
public class ArbitraryFile extends FlowDocument {

    private File localFile;
    private transient byte[] fileBytes;

    protected ArbitraryFile(File localFile) throws IOException {
        super();
        this.localFile = localFile;
        this.fileBytes = Files.readAllBytes(localFile.toPath());
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public File getLocalFile() {
        return localFile;
    }
}
