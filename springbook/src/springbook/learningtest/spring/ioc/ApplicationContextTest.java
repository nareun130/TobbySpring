package springbook.learningtest.spring.ioc;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

import springbook.learningtest.spring.ioc.bean.Hello;
import springbook.learningtest.spring.ioc.bean.StringPrinter;

public class ApplicationContextTest {

	@Test
	public void registerBean() {
		// IoC컨테이너 생성. 생성과 동시에 컨테이너로 동작함.
		StaticApplicationContext ac = new StaticApplicationContext();

		ac.registerSingleton("hello1", Hello.class);// 디폴트 메타정보를 사용해서 싱글톤 빈을 등록

		Hello hello1 = ac.getBean("hello1", Hello.class);
		assertThat(hello1, is(notNullValue()));// 빈을 요청하고 null이 아닌지 확인

		// 빈 메타정보를 담은 오브젝트를 만듦. 빈 클래스는 Hello로 지정
		BeanDefinition helloDef = new RootBeanDefinition(Hello.class);
		helloDef.getPropertyValues().addPropertyValue("name", "Spring");// <property name="name" value="spring"/>에 해당
		ac.registerBeanDefinition("hello2", helloDef);// BeanDefinition으로 등록된 빈이 컨테이너에 의해 만들어지고 프로퍼티 설정이 됐는지 확인

		Hello hello2 = ac.getBean("hello2", Hello.class);
		assertThat(hello2.sayHello(), is("Hello Spring"));

		assertThat(hello1, is(not(hello2)));// 처음 등록한 빈과 두 번째 등록한 빈이 모두 동일한 Hello클래스지만 별개의 오브젝트로 생성됨.

		assertThat(ac.getBeanFactory().getBeanDefinitionCount(), is(2));// 빈 설정 메타정보를 가져올 수도 있음.

	}

	@Test
	public void registerBeanWithDependency() {
		StaticApplicationContext ac = new StaticApplicationContext();

		ac.registerBeanDefinition("printer", new RootBeanDefinition(StringPrinter.class));

		BeanDefinition helloDef = new RootBeanDefinition(Hello.class);
		helloDef.getPropertyValues().addPropertyValue("name", "Spring");// 단순 값을 갖는 프로퍼티 등록

		// 아이디가 printer인 빈에 대한 레퍼런스를 프로퍼티로 등록
		helloDef.getPropertyValues().addPropertyValue("printer", new RuntimeBeanReference("printer"));

		ac.registerBeanDefinition("hello", helloDef);

		Hello hello = ac.getBean("hello", Hello.class);
		hello.print();

		// Hello 클래스의 print()메소드는 DI 된 Printer 타입의 오브젝트에게 요청해서 인사말을 출력
		// 결과를 스트링으로 저장해두는 printer빈을 통해 확인
		assertThat(ac.getBean("printer").toString(), is("Hello Spring"));
	}

	@Test
	public void genericApplicationContext() {
		GenericApplicationContext ac = new GenericApplicationContext();

		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(ac);
		//기본적으로 클래스 패스로 정의된 리소스로부터 파일을 읽는다.(classpath: || file: || http: ) 
		reader.loadBeanDefinitions("springbook/learningtest/spring/ioc/genericApplicationContext.xml");
		

		ac.refresh();//모든 메타정보가 등록이 완료됐으니 애플리케이션을 초기화

		Hello hello = ac.getBean("hello", Hello.class);
		hello.print();

		assertThat(ac.getBean("printer").toString(), is("Hello Spring"));
	}
}
