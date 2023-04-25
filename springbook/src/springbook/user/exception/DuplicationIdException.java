package springbook.user.exception;

public class DuplicationIdException extends RuntimeException {
	public DuplicationIdException(Throwable cause) {
		super(cause);
	}

}
