package util;

/**
 * Created by Netdex on 1/17/2016.
 */
public class Formatter {

    private static String TAB = "    ";

    public static String format(String str) {
        String[] lines = str.replace("\t", TAB).split("\n");
        String text = "";
        int tabIndex = 0;
        for (String line : lines) {
            line = line.trim();

            if (line.startsWith("}") && line.endsWith("{")) {
                tabIndex--;
                for (int i = 0; i < tabIndex; i++)
                    text += TAB;
                text += line + '\n';
                tabIndex++;
            } else if (line.startsWith("}")) {
                tabIndex--;
                for (int i = 0; i < tabIndex; i++)
                    text += TAB;
                text += line + '\n';
            } else if (line.endsWith("{")) {
                for (int i = 0; i < tabIndex; i++)
                    text += TAB;
                tabIndex++;
                text += line + '\n';
            } else {
                for (int i = 0; i < tabIndex; i++)
                    text += TAB;
                text += line + '\n';
            }
            if ((line.startsWith("if") || line.startsWith("for")) && !line.endsWith("{")) {
                text += TAB;
            }
        }
        return text;
    }
}
