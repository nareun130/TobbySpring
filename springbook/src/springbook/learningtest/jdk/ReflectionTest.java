package springbook.learningtest.jdk;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;

import org.junit.Test;

public class ReflectionTest {

	@Test
	public void invokeMethod() throws Exception {
		String name = "Spring";

		// length()
		assertThat(name.length(), is(6));

		Method lengthMethod = String.class.getMethod("length");
		assertThat((Integer) lengthMethod.invoke(name), is(6));

		// charAt()
		assertThat(name.charAt(0), is('S'));

		Method charAtMethod = String.class.getMethod("charAt", int.class);
		assertThat((Character) charAtMethod.invoke(name, 0), is('S'));
	}

	@Test
	public void simpleProxy() {
		// 타킷은 인터페이스를 통해 접근하는 습관을 들이도록!!
		Hello hello = new HelloTarget();
		assertThat(hello.sayHello("Toby"), is("Hello Toby"));
		assertThat(hello.sayHi("Toby"), is("Hi Toby"));
		assertThat(hello.sayThankYou("Toby"), is("Thank You Toby"));

		Hello proxiedHello = new HelloUppercase(new HelloTarget()); // 프록시를 통해 타깃 오브젝트에 접근하도록 구성
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
	}
}
