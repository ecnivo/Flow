package util;

public class DocumentNotFoundException extends Exception {
	public DocumentNotFoundException(String message) {
		super(message);
	}

	public DocumentNotFoundException() {
		super();
	}
}
