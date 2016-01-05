package struct;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;

/**
 * Represents a file of arbitrary extension, cannot be collaborated on
 * <p>
 * Created by Netdex on 12/24/2015.
 */
public class ArbitraryDocument extends FlowDocument {

    private File localFile;
    private transient byte[] fileBytes;

    public ArbitraryDocument(File localFile, FlowFile parent, Date versionDate) throws IOException {
        super(parent, versionDate);
        this.localFile = localFile;
        if (localFile != null)
            fileBytes = Files.readAllBytes(localFile.toPath());
    }

    public ArbitraryDocument() throws IOException {
        this(null, null, new Date());
    }

    /**
     * Gets the contents of this file
     *
     * @return the contents of this file
     */
    public byte[] getFileBytes() {
        return fileBytes;
    }

    /**
     * Gets the local directory to this file
     *
     * @return the local directory to this file
     */
    public File getLocalFile() {
        return localFile;
    }
}
