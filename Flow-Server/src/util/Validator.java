package util;

public class Validator {

	public static boolean validUserName(String str) {
		// TODO Check number of characters
		return str == null ? false : str.matches("[A-Za-z]{1,16}");
	}

	public static boolean validFileName(String str) {
		// TODO update this
		return str == null ? false : str.matches("[A-Za-z]{1,16}");
	}
}
