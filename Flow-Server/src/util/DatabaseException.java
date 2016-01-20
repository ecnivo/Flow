package util;

/**
 * Exception thrown from SQLDatabase methods when an expected result occurs.<br>
 * NOTE: all DatabaseExceptions are required to have a message.
 * 
 * @version January 1st, 2016
 * @author Bimesh De Silva
 *
 */
public class DatabaseException extends Exception {
	public DatabaseException(String message) {
		super(message);
	}
}
