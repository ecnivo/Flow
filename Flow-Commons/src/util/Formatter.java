package util;

/**
 * Created by Netdex on 1/17/2016.
 */
public class Formatter {

    private static String TAB = "  ";

    public static String format(String str) {
        String[] lines = str.replace("\t", TAB).split("\n");
        String text = "";
        int tabIndex = 0;
        for (String line : lines) {
            line = line.replaceFirst("^ +", "");
            for (int i = 0; i < tabIndex; i++)
                text += TAB;
            boolean escape = false;
            boolean inside = false;
            boolean newEscape = false;
            for (char c : line.toCharArray()) {
                switch (c) {
                    case '\\':
                        escape = true;
                        newEscape = true;
                        break;
                    case '{':
                        if (!inside && !escape)
                            tabIndex++;
                        break;
                    case '}':
                        if (!inside && !escape)
                            tabIndex--;
                        break;
                }
                if (escape && !newEscape)
                    escape = false;
                if (newEscape)
                    newEscape = false;
            }
            text += line + '\n';
        }
        return text;
    }
}
