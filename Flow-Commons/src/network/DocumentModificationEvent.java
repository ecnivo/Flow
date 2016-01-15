package network;

/**
 * Created by Netdex on 1/15/2016.
 */
public class DocumentModificationEvent {
    private String type;
    private int line;
    private int idx;
    private String adx;
    private int len;


    public DocumentModificationEvent(String type, int line, int idx, String adx, int len) {
        this.type = type;
        this.line = line;
        this.idx = idx;
        this.adx = adx;
        this.len = len;
    }

    public String getModificationType() {
        return type;
    }

    public int getLineNumber() {
        return line;
    }

    public int getIndex() {
        return idx;
    }

    private String getAddition() {
        return adx;
    }

    private int getLength() {
        return len;
    }
}
