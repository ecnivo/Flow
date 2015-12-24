package struct;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Represents a file of arbitrary extension
 *
 * Created by Netdex on 12/24/2015.
 */
public class ArbitraryFile extends Document {

    private transient File localFile;
    private byte[] fileBytes;

    protected ArbitraryFile(String remotePath, File localFile) throws IOException {
        super(remotePath);
        this.localFile = localFile;
        this.fileBytes = Files.readAllBytes(localFile.toPath());
    }

    public byte[] getFileBytes(){
        return fileBytes;
    }

    public File getLocalFile(){
        return localFile;
    }
}
