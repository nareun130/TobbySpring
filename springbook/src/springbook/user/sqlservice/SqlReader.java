package springbook.user.sqlservice;

public interface SqlReader {
	//SQL을 외부에서 가져와 SqlRegistry에 등록. 다양한 예외가 발생할 수 있겠지만 대부분 복구 불가능한 예외 -> 굳이 예외 선언 x
	void read(SqlRegistry registry);
	
	
}
