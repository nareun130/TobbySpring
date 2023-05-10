package springbook.user.sqlservice;

//SQL을가져오다가 실패하는 경우에 던지는 예외 : 대개 복구 불가 -> 런타임 예외로 정의
public class SqlRetrievalFailureException extends RuntimeException {
	public SqlRetrievalFailureException(String message) {
		super(message);
	}

	// SQL을 가져오는 데 실패한 근본 원인을 담을 수 있게 중첩 예외를 저장할 수 있는 생성자를 만듦.
	public SqlRetrievalFailureException(String message, Throwable cause) {
		super(message, cause);
	}
}
