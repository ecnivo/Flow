import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
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

    public void sendParcelable(Parcelable parcelable) throws IOException {
        byte[]
    }
    public Parcelable receiveParcelable(Parcelable template) throws IOException {
        byte[] dataLen = new byte[4];
        is.read(dataLen);
        int len = byteArrToInteger(dataLen);
        byte[] data = new byte[len];
        is.read(data);
        return template.deserialize(data);
    }

    private static byte[] intToByteArr(int integer){
        return new byte[]{(byte)(integer & 0xFF), };
    }

    private static int byteArrToInteger(byte[] arr) {
        if (arr.length != 4)
            throw new IllegalArgumentException("ARRAY LENGTH MUST BE 4");
        return (arr[0] << 24) & 0xff000000 |
                (arr[1] << 16) & 0x00ff0000 |
                (arr[2] << 8) & 0x0000ff00 |
                (arr[3] << 0) & 0x000000ff;
    }
}