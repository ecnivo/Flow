package test;

import message.Data;
import network.FMLNetworker;
import server.FlowServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Netdex on 1/14/2016.
 */
public class ServerTest {

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        FMLNetworker fmlNetworker = new FMLNetworker("localhost", FlowServer.PORT);
        while (true) {
            String dataStr = br.readLine();
            Data data = new Data();
            String[] dataInParse = dataStr.split(";");
            for (String flag : dataInParse) {
                String[] flagInParse = flag.split("=");
                String key = flagInParse[0];
                Serializable val = flagInParse[1];
                if (flagInParse[1].startsWith("$"))
                    val = UUID.fromString(flagInParse[1].substring(1));
                data.put(key, val);
            }
            fmlNetworker.send(data);
        }
    }
}
