package springbook.learningtest.spring.pointcut;

//포인트컷 테스트용 클래스
public class Target implements TargetInterface {

	@Override
	public void hello() {

	}

	@Override
	public void hello(String a) {

	}

	@Override
	public int plus(int a, int b) {
		return 0;
	}

	@Override
	public int minus(int a, int b) throws RuntimeException {
		return 0;
	}

	public void method() {

	}

}
