package callback;

/**
 * Created by Netdex on 1/15/2016.
 */
public class DocumentCallbackEvent extends CallbackEvent {

    public enum DocumentCallbackType {
        INSERT,
        DELETE
    }

    private DocumentCallbackType type;
    private int line;
    private int idx;
    private String add;
    private int len;

    public DocumentCallbackType getType() {
        return type;
    }

    public int getLine() {
        return line;
    }

    public int getIdx() {
        return idx;
    }

    public String getAdd() {
        return add;
    }

    public int getLen() {
        return len;
    }

    protected DocumentCallbackEvent(DocumentCallbackType type, int line, int idx, String add, int len) {
        this.type = type;
        this.line = line;
        this.idx = idx;
        this.add = add;
        this.len = len;
    }
}
