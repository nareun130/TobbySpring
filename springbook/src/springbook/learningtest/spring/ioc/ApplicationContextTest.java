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
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import springbook.learningtest.spring.ioc.bean.Hello;
import springbook.learningtest.spring.ioc.bean.Printer;
import springbook.learningtest.spring.ioc.bean.StringPrinter;

public class ApplicationContextTest {

	// 현재 클래스의 패키지 정보를 클래스패스 형식으로 만들어서 미리 저장
	private String basePath = StringUtils.cleanPath(ClassUtils.classPackageAsResourcePath(getClass())) + "/";

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
		// 기본적으로 클래스 패스로 정의된 리소스로부터 파일을 읽는다.(classpath: || file: || http: )
		reader.loadBeanDefinitions("springbook/learningtest/spring/ioc/genericApplicationContext.xml");

		ac.refresh();// 모든 메타정보가 등록이 완료됐으니 애플리케이션을 초기화

		Hello hello = ac.getBean("hello", Hello.class);
		hello.print();

		assertThat(ac.getBean("printer").toString(), is("Hello Spring"));
	}

	@Test
	public void contextHierachy() {
		// 부모 컨텍스트 생성
		ApplicationContext parent = new GenericXmlApplicationContext(basePath + "parentContext.xml");

		// 자식 컨텍스트 생성 -> 부모 컨택스트를 지정해주면서 생성
		GenericApplicationContext child = new GenericApplicationContext(parent);

		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(child);
		reader.loadBeanDefinitions(basePath + "childContext.xml");
		child.refresh();

		// childContext.xml에는 printer라는 빈이 존재하지 않기 때문에 부모 컨텍스트로 검색이 넘어감.
		Printer printer = child.getBean("printer", Printer.class);
		assertThat(printer, is(notNullValue()));

		Hello hello = child.getBean("hello", Hello.class);
		assertThat(hello, is(notNullValue()));

		hello.print();
		assertThat(printer.toString(), is("Hello Child"));// getBean()으로 가져온 hello 빈은 자식 컨텍스트에 존재하는 것

	}

}
