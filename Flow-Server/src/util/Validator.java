package util;

public class Validator {

	private static final char[] blacklist = { '\'', ';' };

	public static boolean validUserName(String str) {
		// TODO update number of characters
		return str == null ? false : str.matches("[A-Za-z0-9]{1,16}");
	}

	public static boolean validFileName(String str) {
		// TODO update the regex
		return str == null ? false : str.matches("[A-Za-z]{1,16}");
	}

	public boolean remove(String message) {
		int length = message.length();
		for (int i = 0; i < length; i++) {
			char c = message.charAt(i);
			for (int j = 0; j < blacklist.length; j++) {
				if (c == blacklist[j])
					return false;
			}
		}
		return true;
	}
}
