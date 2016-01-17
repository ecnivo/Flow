package server;

import struct.VersionText;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Manages versions of files
 *
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
    private HashMap<UUID, UUID> parentFile;

    private VersionManager() {
        this.loadedDocuments = new HashMap<>();
        this.parentFile = new HashMap<>();
    }

    /**
     * Loads all versions into memory
     *
     * @param fileDir Location of files
     * @return success or not
     */
    public boolean loadAllDocuments(File fileDir) {
        L.info("loading all document versions");
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
                        String versionName = versionFile.getName();
                        versionName = versionName.substring(0, versionName.lastIndexOf('.'));
                        String fileName = fileStateDirectory.getName();
                        this.addTextVersion(UUID.fromString(fileName), UUID.fromString(versionName), loadedVersion);
                    }
                }
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Flushes all versions to disk
     * @param fileDir Directory to store
     * @return success or not
     */
    public boolean flushToDisk(File fileDir) {
        // TODO doesn't remove versions that were deleted!
        for (UUID versionUUID : loadedDocuments.keySet()) {
            UUID parentFileUUID = parentFile.get(versionUUID);
            if (!flushToDisk(fileDir, parentFileUUID, versionUUID))
                return false;
        }
        return true;
    }

    /**
     * Flushes a single version to disk
     * @param fileDir File directory
     * @param fileUUID File UUID
     * @param versionUUID Version UUID
     * @return success or not
     */
    public boolean flushToDisk(File fileDir, UUID fileUUID, UUID versionUUID) {
        File versionPath = new File(new File(fileDir, fileUUID.toString())
                , versionUUID.toString() + "." + DataManagement.TEXT_FILE_EXT);
        versionPath.getParentFile().mkdirs();
        VersionText versionText = loadedDocuments.get(versionUUID);
        try {
            PrintStream ps = new PrintStream(versionPath);
            ps.print(versionText.getDocumentText());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Add a text version
     * @param fileUUID File UUID
     * @param versionUUID Version UUID
     * @param versionText Text data
     * @return success or not
     */
    public boolean addTextVersion(UUID fileUUID, UUID versionUUID, VersionText versionText) {
        if (!loadedDocuments.containsKey(versionUUID)) {
            loadedDocuments.put(versionUUID, versionText);
            parentFile.put(versionUUID, fileUUID);
            return true;
        }
        L.warning("text version " + versionUUID + " was added but it already exists!");
        return false;
    }

    /**
     * Removes a text version
     * @param versionUUID Version UUID
     * @return success or not
     */
    public boolean removeTextVersion(UUID versionUUID) {
        if (loadedDocuments.containsKey(versionUUID)) {
            L.info("removed text version " + versionUUID + " into memory map");
            loadedDocuments.put(versionUUID, null);
            parentFile.put(versionUUID, null);
            return true;
        }
        L.warning("text version " + versionUUID + " was removed but it doesn't exist!");
        return false;
    }

    /**
     * Gets text version by UUID
     * @param versionUUID Version UUID
     * @return success or not
     */
    public VersionText getTextByVersionUUID(UUID versionUUID) {
        return loadedDocuments.get(versionUUID);
    }
}
