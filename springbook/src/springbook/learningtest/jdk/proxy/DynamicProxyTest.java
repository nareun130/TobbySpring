package springbook.learningtest.jdk.proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactoryBean;

import springbook.learningtest.jdk.UppercaseHandler;

public class DynamicProxyTest {
	@Test
	public void simpleProxy() {
		// jdk 다이내믹 프록시 생성
		Hello proxiedHello = (Hello) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { Hello.class },
				new UppercaseHandler(new HelloTarget()));

	}

	@Test
	public void proxyFactoryBean() {
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget());
		pfBean.addAdvice(new UppercaseAdvice());// 부가기능을 담은 어드바이스를 추가. -> 여러개 추가 가능

		Hello proxiedHello = (Hello) pfBean.getObject();
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
	}

	static class UppercaseAdvice implements MethodInterceptor {

		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			// 리플렉션의 Method와 달리 메소드 실행 시 타깃 오브젝트를 전달할 필요x
			// MethodInvocation은 메소드 정보와 함께 타깃 오브젝트를 알고 있기 때문
			String ret = (String) invocation.proceed();
			return ret.toUpperCase(); // 부가기능 적용
		}

	}

	static interface Hello {// 타깃과 프록시가 구현할 인터페이스
		String sayHello(String name);

		String sayHi(String name);

		String sayThankYou(String name);

	}

	static class HelloTarget implements Hello {

		@Override
		public String sayHello(String name) {
			return "Hello " + name;
		}

		@Override
		public String sayHi(String name) {
			return "Hi " + name;
		}

		@Override
		public String sayThankYou(String name) {
			return "Thank You " + name;
		}

	}
}
