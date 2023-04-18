package springbook.user.dao;

import java.sql.SQLException;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import springbook.user.domain.User;

public class UserDaoConnectionCountingTest {
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CountingDaoFactory.class);
		UserDao dao = context.getBean("userDao",UserDao.class);
		
		User user = new User();
		user.setId("nareun");
		user.setName("성호");
		user.setPassword("130");
		
		dao.add(user);
		
		User user2 = dao.get(user.getId());
		
		System.out.println(user2.getName() + "," + user2.getPassword());
		System.out.println(user2.getId()+"조회 완료");
		
		CountingConnectionMaker ccm = context.getBean("connectionMaker",CountingConnectionMaker.class);
		System.out.println("Connection counter : " + ccm.getCounter());
		
		
	}
}
