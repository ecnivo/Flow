import util.Formatter;

import java.io.IOException;

/**
 * Created by Gordon Guan on 1/17/2016.
 */
public class CommonsTest {
    public static void main(String[] args) throws IOException {
        String code = "package util;\n" +
                "\n" +
                "/**\n" +
                " * Created by Gordon Guan on 1/17/2016.\n" +
                " */\n" +
                "public class Formatter {\n" +
                "\n" +
                "    private static String TAB = \"    \";\n" +
                "    public static String format(String str) {\n" +
                "        str = str.replaceAll(\"\\n\", \"\");\n" +
                "        int tabIndex = 0;\n" +
                "        String form = \"\";\n" +
                "        for (char c : str.toCharArray()) {\n" +
                "            boolean newLine = false;\n" +
                "            if(c == '{' || c == '}'){\n" +
                "                tabIndex += c == '{' ? 1 : -1;\n" +
                "                newLine = true;\n" +
                "            }else if(c == ';'){\n" +
                "                newLine = true;\n" +
                "            }\n" +
                "            if(newLine){\n" +
                "                form += '\\n';\n" +
                "                for(int i = 0; i < tabIndex; i++){\n" +
                "                    form += TAB;\n" +
                "                }\n" +
                "            }\n" +
                "            form += c;\n" +
                "        }\n" +
                "        return form;\n" +
                "    }\n" +
                "}\n";
        System.out.println(Formatter.format(code));
    }
}
