package springbook.learningtest.spring.pointcut;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

public class PointcutExpressionTest {

	// 메소드 시그니처를 이용한 포인트컷 표현식 테스트
	@Test
	public void methodSignaturePointcut() throws NoSuchMethodException, SecurityException {
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression(
				"execution(public int " + "springbook.learningtest.spring.pointcut.Target.minus(int,int) "
						+ "throws java.lang.RuntimeException)");

		// Target.minus()
		// 클래스 필터와 메소드 매처를 가져와 각각 비교한다.
		assertThat(pointcut.getClassFilter().matches(Target.class)
				&& pointcut.getMethodMatcher().matches(Target.class.getMethod("minus", int.class, int.class), null),
				is(true));// 포인트컷 조건 통과

		// Target.plus()
		assertThat(pointcut.getClassFilter().matches(Target.class)
				&& pointcut.getMethodMatcher().matches(Target.class.getMethod("plus", int.class, int.class), null),
				is(false));// 메소드 매처에서 실패
		// Bean.method()
		assertThat(pointcut.getClassFilter().matches(Bean.class)
				&& pointcut.getMethodMatcher().matches(Target.class.getMethod("method"), null), is(false));// 클래스 필터에서
																											// 실패
	}
}
