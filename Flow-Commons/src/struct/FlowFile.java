package struct;

import java.io.Serializable;
import java.util.Date;
import java.util.TreeMap;

/**
 * Created by Netdex on 12/25/2015.
 */
public class FlowFile implements Serializable {

    private String remotePath;
    private String remoteName;

    private TreeMap<Date, Document> versions;

    public FlowFile(String remotePath, String remoteName) {
        this.remoteName = remoteName;
        this.remotePath = remotePath;
        this.versions = new TreeMap<>();
    }

    public Document latest() {
        return versions.get(versions.lastKey());
    }

    public void addVersion(Date date, Document document) {
        versions.put(date, document);
    }

    public String getRemotePath() {
        return remotePath;
    }

    public String getRemoteName() {
        return remoteName;
    }
}

