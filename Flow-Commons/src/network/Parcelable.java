package network;

/**
 * An interface representing a piece of data which can be serialized into a series of bytes and back into the original object
 * Created by Netdex on 12/17/2015.
 */
public interface Parcelable {
    /**
     * Turns this parcelable into a series of bytes
     *
     * @return the byte representation of this parcelable
     */
    public byte[] serialize();

    /**
     * Creates a parcelable from a series of bytes
     *
     * @param data The byte representation of this parcelable
     * @return The parcelable constructed from the byte representation
     */
    public Parcelable deserialize(byte[] data);
}
