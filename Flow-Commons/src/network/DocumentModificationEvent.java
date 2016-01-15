package network;

/**
 * Created by Netdex on 1/15/2016.
 */
public class DocumentModificationEvent {
    static enum DocumentModificationType {
        INSERT,
        DELETE
    }

    private DocumentModificationType type;
    private int line;
    private int idx;
    private String adx;
    private int len;


    public DocumentModificationEvent(DocumentModificationType type, int line, int idx, String adx, int len) {
        this.type = type;
        this.line = line;
        this.idx = idx;
        this.adx = adx;
        this.len = len;
    }

    /**
     * Either INSERT or DELETE
     *
     * @return
     */
    public DocumentModificationType getModificationType() {
        return type;
    }

    /**
     * The line number of the operation
     * @return
     */
    public int getLineNumber() {
        return line;
    }

    /**
     * The index of the operation
     * @return
     */
    public int getIndex() {
        return idx;
    }

    /**
     * Get the
     * @return
     */
    private String getAddition() {
        return adx;
    }

    private int getLength() {
        return len;
    }
}
