package springbook.learningtest.user;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springbook.user.dao.DaoFactory;
import springbook.user.dao.UserDaoJdbc;
import springbook.user.domain.User;

//스프링이 싱글톤 방식으로 빈의 오브젝트를 만든다는 것을 검증해보는 테스트
public class UserDaoTest1 {

	UserDaoJdbc dao1;
	UserDaoJdbc dao2;

	ApplicationContext context;

	@Before
	public void setUp() {

		this.context = new AnnotationConfigApplicationContext(DaoFactory.class);
		dao1 = context.getBean("userDao", UserDaoJdbc.class);
		dao2 = context.getBean("userDao", UserDaoJdbc.class);
	}

	@Test
	public void equalTest() {
		assertThat(dao1, is(dao2));

	}

	public static void main(String[] args) {
		JUnitCore.main("springbook.user.dao.UserDaoTest");
	}
}
