package struct;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Represents a file of arbitrary extension, cannot be collaborated on
 * <p>
 * Created by Netdex on 12/24/2015.
 */
public class ArbitraryDocument extends FlowDocument {

    private transient File localFile;
    private byte[] fileBytes;

    public ArbitraryDocument(File localFile) throws IOException {
        super();
        this.localFile = localFile;
        if(localFile != null)
            fileBytes = Files.readAllBytes(localFile.toPath());
    }

    public ArbitraryDocument() throws IOException {
        this(null);
    }

    /**
     * Gets the contents of this file
     * @return the contents of this file
     */
    public byte[] getFileBytes(){
        return fileBytes;
    }

    /**
     * Gets the local directory to this file
     * @return the local directory to this file
     */
    public File getLocalFile() {
        return localFile;
    }
}
