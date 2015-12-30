package util;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.file.Files;

/**
 * Created by Netdex on 12/30/2015.
 */
public class Checksum {

    public static long getFileChecksum(File file) throws IOException {
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        return getByteChecksum(fileBytes);
    }

    public static long getByteChecksum(byte[] arr){
        long chksum = 0;
        for(int i = 0; i < arr.length; i++){
            chksum += arr[i];
            chksum *= 179424691;
        }
        return chksum;
    }

    public static long getObjectChecksum(Serializable serializable) throws IOException {
        /* TODO FIX
        ByteBuffer buf = ByteBuffer.allocate(65535);
        ObjectOutputStream oos = new ObjectOutputStream();
        oos.writeObject(serializable);
        return getByteChecksum(bos.getBytes());*/
        throw new NotImplementedException();
    }
}
