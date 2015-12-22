package network;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * A wrapper around a socket which has the ability to send Parcelables over the network
 * <p>
 * Created by Netdex on 12/17/2015.
 */
public class PackageSocket extends Socket {

    private Socket socket;

    private OutputStream os;
    private InputStream is;

    public PackageSocket(Socket socket) throws IOException {
        this.socket = socket;
        this.os = socket.getOutputStream();
        this.is = socket.getInputStream();
    }

    /**
     * Sends a parcelable across the network
     *
     * @param parcelable The parcelable to send
     * @throws IOException When something bad happens
     */
    public void sendParcelable(Parcelable parcelable) throws IOException {
        byte[] data = parcelable.serialize();
        byte[] dataLen = intToByteArr(data.length);
        os.write(dataLen);
        os.write(data);
        os.flush();
    }

    /**
     * Receives a parcelable from the network
     *
     * @return The parcelable received from the network
     * @throws IOException                  When something bad happens
     * @throws MalformedParcelableException When input data does not correspond to the provided template
     */
    public Parcelable receiveParcelable() throws IOException, MalformedParcelableException {
        byte[] dataLen = new byte[4];
        is.read(dataLen);
        int len = byteArrToInteger(dataLen);
        byte[] data = new byte[len];
        is.read(data);
        throw new NotImplementedException();
    }

    /**
     * Converts an integer into 4 bytes
     *
     * @param integer The integer to convert
     * @return 4 bytes, little endian
     */
    private static byte[] intToByteArr(int integer) {
        ByteBuffer b = ByteBuffer.allocate(4);
        b.putInt(integer);
        return b.array();
    }

    /**
     * Converts 4 bytes into an integer
     *
     * @param arr 4 bytes, little endian
     * @return The 4 bytes as an integer
     */
    private static int byteArrToInteger(byte[] arr) {
        if (arr.length != 4)
            throw new IllegalArgumentException("Byte array size is not 4");
        return (arr[0] << 24) & 0xff000000 |
                (arr[1] << 16) & 0x00ff0000 |
                (arr[2] << 8) & 0x0000ff00 |
                (arr[3] << 0) & 0x000000ff;
    }
}
