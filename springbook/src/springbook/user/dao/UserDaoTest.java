package springbook.user.dao;

import java.sql.SQLException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import springbook.user.domain.User;

public class UserDaoTest {
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
//		ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
		// xml을 사용하는 애플리케이션 컨텍스트
		ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
		UserDao dao = context.getBean("userDao", UserDao.class);

		User user = new User();
		user.setId("whiteship2");
		user.setName("백기선");
		user.setPassword("married");

		dao.add(user);

		User user2 = dao.get(user.getId());

		System.out.println(user2.getName() + "," + user2.getPassword());
		System.out.println(user2.getId() + "조회 완료");

	}
}