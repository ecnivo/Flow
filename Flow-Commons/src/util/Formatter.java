package util;

/**
 * Formats code to look nice
 * Created by Gordon Guan on 1/17/2016.
 */
public class Formatter {

    /**
     * Weakly formats a string of code
     *
     * @param str the code to format
     * @return formatted code
     */
    public static String format(String str) {
        // Remove tabs, split by newlines
        String TAB = "    ";
        String[] lines = str.replace("\t", TAB).split("\n");
        String text = "";
        int tabIndex = 0;
        for (String line : lines) {
            line = line.trim();

            // Check if the line is an else if styled line
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
            // Check if the line is a single line if or for without braces
            if ((line.startsWith("if") || line.startsWith("for")) && !line.endsWith("{")) {
                text += TAB;
            }
        }
        return text;
    }
}
