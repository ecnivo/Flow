package util;

public class ProjectNotFoundException extends Exception {
	public ProjectNotFoundException(String message) {
		super(message);
	}

	public ProjectNotFoundException() {
		super();
	}
}
