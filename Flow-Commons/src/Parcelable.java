/**
 * Created by Netdex on 12/17/2015.
 */
public interface Parcelable {
    public byte[] serialize();
    public Parcelable deserialize(byte[] data);
}
