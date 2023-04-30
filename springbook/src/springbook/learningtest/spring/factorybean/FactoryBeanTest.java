package springbook.learningtest.spring.factorybean;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration // 설정파일 이름을 지정않아면 클래스이름 + "context.xml이 디폴드로 사용됨.
public class FactoryBeanTest {
	@Autowired
	ApplicationContext context;

	@Test
	public void getMessageFromFactoryBean() {
		// 팩토리 빈이 만들어주는 빈 오브젝트를 가져옴.
//		Object message = context.getBean("message");
//		assertThat(message, is(Message.class));
//		assertThat(((Message) message).getText(), is("Factory Bean"));

		// 팩토리 빈자체를 가지고 오는 경우
		Object factory = context.getBean("&message");
		assertThat(factory, is(MessageFactoryBean.class));
	}
}
