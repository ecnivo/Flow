package struct;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Represents a file of arbitrary extension
 * <p>
 * Created by Netdex on 12/24/2015.
 */
public class ArbitraryDocument extends FlowDocument {

    private File localFile;

    protected ArbitraryDocument(File localFile) throws IOException {
        super();
        this.localFile = localFile;
    }

    public File getLocalFile() {
        return localFile;
    }
}
