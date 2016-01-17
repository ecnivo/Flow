package util;

public class Validator {

	private static final char[] blacklist = { '\'', ';' };

	public static boolean validUserName(String str) {
		// TODO update number of characters
		return str == null ? false : str.matches("[A-Za-z0-9]{1,16}");
	}

	public static boolean validFileName(String str) {
		// TODO update the regex
		return str == null ? false : str.matches("[A-Za-z0-9_\\-\\.]{1,16}");
	}

	/**
	 * Verifies if the message is safe to be put into an SQL statement.
	 * 
	 * @param message
	 *            the string to check.
	 * @return whether or not the string is SQL safe.
	 */
	public boolean verifySQLSafety(String message) {
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
