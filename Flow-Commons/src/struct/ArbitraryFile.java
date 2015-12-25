package struct;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

/**
 * Represents a file of arbitrary extension
 * <p>
 * Created by Netdex on 12/24/2015.
 */
public class ArbitraryFile extends Document {

    private File localFile;
    private transient byte[] fileBytes;

    protected ArbitraryFile(String remotePath, String remoteFile, File localFile, UUID uuid) throws IOException {
        super(remotePath, remoteFile, uuid);
        this.localFile = localFile;
        this.fileBytes = Files.readAllBytes(localFile.toPath());
    }

    protected ArbitraryFile(String remotePath, String remoteFile, File localFile) throws IOException {
        this(remotePath, remoteFile, localFile, UUID.randomUUID());
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public File getLocalFile() {
        return localFile;
    }
}
