package test;

import message.Data;
import network.FMLNetworker;
import server.FlowServer;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Gordon Guan on 1/14/2016.
 */
public class ServerTest {

    public static void main(String[] args) throws Exception {
        /*
        SMALL UTILITY TO TEST THE FUNCTION OF THE SERVER.
        READS FROM STANDARD INPUT, AND WRITES TO LOCAL SERVER ON NETWORK.

        SYNTAX:
        send
        key1=value1;key2=value2;key3=$uuid1
        ALL ARGUMENTS ARE STRINGS UNLESS PRECEEDED BY A $, WHERE IT BECOMES A UUID.

        EX.
        send
        type=list_projects;session_id=$3ca58930-967d-437f-8f0f-3ac5eee4429b

        set
        variable_name=VALUE
         */
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        FMLNetworker fmlNetworker = new FMLNetworker("localhost", FlowServer.PORT, FlowServer.ARC_PORT);
        HashMap<String, String> variables = new HashMap<>();
        while (true) {
            String cmd = br.readLine();
            switch (cmd) {
                case "send":
                    String dataStr = br.readLine();
                    for (String key : variables.keySet()) {
                        dataStr = dataStr.replace("%" + key + "%", variables.get(key));
                    }
                    Data data = new Data();
                    String[] dataInParse = dataStr.split(";");
                    for (String flag : dataInParse) {
                        String[] parsedFlags = flag.split("=");

                        String key = parsedFlags[0];
                        String value = parsedFlags[1];

                        Serializable val;
                        if (value.startsWith("$"))
                            val = UUID.fromString(parsedFlags[1].substring(1));
                        else if (value.startsWith("\\")) {
                            File f = new File(parsedFlags[1].substring(1));
                            FileInputStream fis = new FileInputStream(f);
                            byte[] buf = new byte[(int) f.length()];
                            fis.read(buf);
                            val = buf;
                        } else
                            val = parsedFlags[1];
                        data.put(key, val);
                    }
                    fmlNetworker.send(data);
                    break;
                case "set":
                    String varStr = br.readLine();
                    String[] parsedVar = varStr.split("=");
                    variables.put(parsedVar[0], parsedVar[1]);
                    System.err.println(parsedVar[0] + " set to " + parsedVar[1]);
                    break;
            }

        }
    }
}
