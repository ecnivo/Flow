
package util;

/**
 * Validation class containing prepared regular expressions for easy
 * verification of client inputed data.
 * 
 * @author Bimesh De Silva
 *
 */
public class Validator {

	private static final char[] blacklist = { '\'', ';' };

	/**
	 * Standard for usernames and passwords for the database.<br>
	 * <br>
	 * Character range: 1 - 16 <br>
	 * Character types allowed: any letters, numbers, and the character '-'
	 * 
	 * @param str
	 *            the string to verify
	 * @return whether or not the string fits the specified conditions.
	 */
	public static boolean validIdentification(String str) {
		return str == null ? false : str.matches("[A-Za-z0-9\\-]{1,16}");
	}

	/**
	 * Standard for a valid file name for Flow. <br>
	 * <br>
	 * Character range: 1-192 for the file name, 0-16 for the extension<br>
	 * Character types allowed: any letters, numbers, and the characters '-' or
	 * ' ' in the name, and only letters or numbers allowed in the extension.
	 * <br>
	 * Note: the file name and extension must be separated by a '.' character
	 * for it to be recognized. Otherwise the character limit will be capped at
	 * 192.
	 * 
	 * @param str
	 *            the string to verify.
	 * @return whether or not the string fits the specified conditions.
	 */
	public static boolean validFileName(String str) {
		return str != null && str.matches("[A-Za-z0-9_\\- ]{1,192}([\\.]{1}[A-Za-z0-9]{0,16})?");
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
			for (char aBlacklist : blacklist) {
				if (c == aBlacklist)
					return false;
			}
		}
		return true;
	}
}
