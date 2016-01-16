package callback;

import struct.VersionText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by Netdex on 1/16/2016.
 */
public class VersionManager {
    private static Logger L = Logger.getLogger("VersionManager");
    private static VersionManager instance;

    public static VersionManager getInstance() {
        if (instance == null)
            instance = new VersionManager();
        return instance;
    }

    private HashMap<UUID, VersionText> loadedDocuments;

    private VersionManager() {
        this.loadedDocuments = new HashMap<>();
    }

    public boolean loadAllDocuments(File fileDir) {
        try {
            for (File fileStateDirectory : fileDir.listFiles()) {
                if (fileStateDirectory.isDirectory()) {
                    for (File versionFile : fileStateDirectory.listFiles()) {
                        BufferedReader br = new BufferedReader(new FileReader(versionFile));
                        String line;
                        String txt = "";
                        while ((line = br.readLine()) != null) {
                            txt += line;
                        }
                        VersionText loadedVersion = new VersionText();
                        loadedVersion.setDocumentText(txt);
                        String fileName = versionFile.getName();
                        L.info("loaded version file " + fileName + " into memory");
                        fileName = fileName.substring(0, fileName.lastIndexOf('.'));
                        this.addTextVersion(UUID.fromString(fileName), loadedVersion);
                    }
                }
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public boolean addTextVersion(UUID versionUUID, VersionText versionText) {
        if (!loadedDocuments.containsKey(versionUUID)) {
            L.info("added text version " + versionUUID + " to memory map");
            loadedDocuments.put(versionUUID, versionText);
            return true;
        }
        L.warning("text version " + versionUUID + " was added but it already exists!");
        return false;
    }

    public boolean removeTextVersion(UUID versionUUID) {
        if (loadedDocuments.containsKey(versionUUID)) {
            L.info("removed text version " + versionUUID + " into memory map");
            loadedDocuments.put(versionUUID, null);
            return true;
        }
        L.warning("text version " + versionUUID + " was removed but it doesn't exist!");
        return false;
    }

    public VersionText getTextByVersionUUID(UUID versionUUID) {
        return loadedDocuments.get(versionUUID);
    }
}
