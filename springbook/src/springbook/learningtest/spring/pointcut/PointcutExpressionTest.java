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

	// 포인트컷 표현식 테스트
	@Test
	public void pointcut() throws Exception {
		targetClassPointcutMatches("execution(* *(..))", true, true, true, true, true, true);
		targetClassPointcutMatches("execution(* hello(..))", true, true, false, false, false, false);
		targetClassPointcutMatches("execution(* hello(String))", false, true, false, false, false, false);
		targetClassPointcutMatches("execution(* meth*(..))", false, false, false, false, true, true);
		targetClassPointcutMatches("execution(* *(int,int))", false, false, true, true, false, false);
		targetClassPointcutMatches("execution(* *())", true, false, false, false, true, true);
		targetClassPointcutMatches("execution(* springbook.learningtest.spring.pointcut.Target.*(..))", true, true,
				true, true, true, false);
		targetClassPointcutMatches("execution(* springbook.learningtest.spring.pointcut.*.*(..))", true, true, true,
				true, true, true);
		targetClassPointcutMatches("execution(* springbook..*.*(..))", true, true, true, true, true, true);
		targetClassPointcutMatches("execution(* com..*.*(..))", false, false, false, false, false, false);
		targetClassPointcutMatches("execution(* *..Target.*(..))", true, true, true, true, true, false);
		targetClassPointcutMatches("execution(* *..Tar*.*(..))", true, true, true, true, true, false);
		targetClassPointcutMatches("execution(* *..*get.*(..))", true, true, true, true, true, false);
		targetClassPointcutMatches("execution(* *..B*.*(..))", false, false, false, false, false, true);
		targetClassPointcutMatches("execution(* *..TargetInterface.*(..))", true, true, true, true, false, false);
		targetClassPointcutMatches("execution(* *(..) throws Runtime*)", false, false, false, true, false, true);
		targetClassPointcutMatches("execution(int *(..))", false, false, true, true, false, false);
		targetClassPointcutMatches("execution(void *(..))", true, true, false, false, true, true);
	}

	// 포인트컷과 메소드를 비교해주는 테스트 헬퍼 메소드
	public void pointcutMatches(String expression, Boolean expected, Class<?> clazz, String methodName,
			Class<?>... args) throws Exception {// 마지막 args는 파라미터
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression(expression);

		assertThat(pointcut.getClassFilter().matches(clazz)

				&& pointcut.getMethodMatcher().matches(clazz.getMethod(methodName, args), null), is(expected));
	}

	public void targetClassPointcutMatches(String expression, boolean... expected) throws Exception {

		pointcutMatches(expression, expected[0], Target.class, "hello");
		pointcutMatches(expression, expected[1], Target.class, "hello", String.class);
		pointcutMatches(expression, expected[2], Target.class, "plus", int.class, int.class);
		pointcutMatches(expression, expected[3], Target.class, "minus", int.class, int.class);
		pointcutMatches(expression, expected[4], Target.class, "method");
		pointcutMatches(expression, expected[5], Bean.class, "method");
	}
}
