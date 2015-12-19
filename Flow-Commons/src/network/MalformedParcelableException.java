package network;

/**
 * An exception thrown when a parcelable cannot be deserialized from data due to invalid structure
 * Created by Netdex on 12/18/2015.
 */
public class MalformedParcelableException extends Exception {
    public MalformedParcelableException(String msg){
        super(msg);
    }
    public MalformedParcelableException(){

    }
}
