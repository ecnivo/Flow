package util;

/**
 * Created by Netdex on 1/17/2016.
 */
public class Formatter {

    private static String TAB = "  ";

    public static String format(String str) {
        str = str.replaceAll("\n", "");
        String fstr = "";
        boolean escapedStr = false;
        boolean strFound = false;
        boolean space = false;
        for (char c : str.toCharArray()) {
            if (c == '"') {
                if (!escapedStr)
                    strFound = !strFound;
                else
                    escapedStr = false;
            } else if (c == '\\')
                escapedStr = true;
            else if (c == ' ')
                space = true;
            else {
                if (space) {
                    fstr += ' ';
                    space = false;
                }
            }
            if (c != ' ')
                fstr += c;
        }

        int tabIndex = 0;
        String form = "";
        int ignore = 0;
        int escaped = 0;
        int bracketDepth = 0;
        for (char c : fstr.toCharArray()) {
            int newLine = 0;
            if (c == '{' || c == '}') {
                if (ignore == 0) {
                    tabIndex += c == '{' ? 1 : -1;
                    newLine = c == '{' ? 1 : -1;
                }
            } else if (c == ';') {
                newLine = 1;
            } else if (c == '\'') {
                if (escaped == 0)
                    ignore = ignore == 0 ? 1 : ignore == 1 ? 0 : ignore;
            } else if (c == '\"') {
                if (escaped == 0)
                    ignore = ignore == 2 ? 0 : 2;
            } else if (c == '\\') {
                escaped = 2;
            } else if (c == '(') {
                bracketDepth++;
            } else if (c == ')') {
                bracketDepth--;
            }
            if (newLine != 0 && ignore == 0 && bracketDepth == 0) {
                if (newLine == 1) {
                    System.out.print(c);
                    System.out.print('\n');
                    for (int i = 0; i < tabIndex; i++) {
                        System.out.print(TAB);
                    }
                } else if (newLine == -1) {
                    System.out.print('\n');
                    for (int i = 0; i < tabIndex; i++) {
                        System.out.print(TAB);
                    }
                    System.out.print(c);
                }
            } else {
                System.out.print(c);
            }
            if (escaped > 0)
                escaped--;
        }
        return form;
    }
}
