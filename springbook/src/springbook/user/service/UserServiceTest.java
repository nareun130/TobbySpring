package springbook.user.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static springbook.user.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserService.MIN_RECCOMEND_FOR_GOLD;

import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-applicationContext.xml")
public class UserServiceTest {

	@Autowired
	PlatformTransactionManager transactionManager;

	@Autowired
	UserService userService;

	@Autowired
	UserDao userDao;

	@Autowired
	DataSource dataSource;

	@Autowired
	MailSender mailSender;
	
	List<User> users;

	@Before
	public void setUp() {
		users = Arrays.asList(
				new User("bumjin", "박범진", "p1", "user1@ksug.org", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0),
				new User("joytouch", "강명성", "p2", "user2@ksug.org", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
				new User("erwins", "신승한", "p3", "user3@ksug.org", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD - 1),
				new User("madnite1", "이상호", "p4", "user4@ksug.org", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD),
				new User("green", "오민규", "p5", "user5@ksug.org", Level.GOLD, 100, Integer.MAX_VALUE));
	}

	@Test
	public void upgradeLevels() throws Exception {
		userDao.deleteAll();
		for (User user : users)
			userDao.add(user);

		userService.upgradeLevels();

		checkLevelUpgraded(users.get(0), false);
		checkLevelUpgraded(users.get(1), true);
		checkLevelUpgraded(users.get(2), false);
		checkLevelUpgraded(users.get(3), true);
		checkLevelUpgraded(users.get(4), false);
	}

	@Test
	public void add() {
		userDao.deleteAll();

		User userWithLevel = users.get(4);
		User userWithoutLevel = users.get(0);
		userWithoutLevel.setLevel(null);

		userService.add(userWithLevel);
		userService.add(userWithoutLevel);

		User userWithLevelRead = userDao.get(userWithLevel.getId());
		User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

		assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
		assertThat(userWithoutLevelRead.getLevel(), is(Level.BASIC));

	}

	@Test
	public void upgradeAllOrNothing() throws Exception {

		// 예외를 발생시킬 네 번째 사용자의 id를 넣어 테스트용 UserService대역 오브젝트를 생성
		UserService testUserService = new TestUserService(users.get(3).getId());
		testUserService.setUserDao(this.userDao); // userDao를 수동 DI
		testUserService.setTransactionManager(transactionManager);// userService 빈의 프로퍼티 설정과 동일한 수동 DI
		testUserService.setMailSender(mailSender);

		userDao.deleteAll();
		for (User user : users)
			userDao.add(user);
		try {
			testUserService.upgradeLevels();// 여기서 Exception을 던져줘서 fail이 아닌 catch문을 타고 checkLevelUpgraded를 타는 듯
			fail("TestUserServiceException expected");// upgradeLevels가 정상적으로 종료되면 fail때문에 테스트가 실패할 것

		} catch (TestUserServiceException e) {

		}
		checkLevelUpgraded(users.get(1), false);
	}

	// checkLevel의 중복 작업을 줄여줄 메서드
	private void checkLevelUpgraded(User user, boolean upgraded) {
		User userUpdate = userDao.get(user.getId());
		if (upgraded) {
			assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
		} else {
			assertThat(userUpdate.getLevel(), is(user.getLevel()));
		}
	}

	// 테스트용 서비스를 내부 클래스로 구현
	static class TestUserService extends UserService {
		private String id;

		private TestUserService(String id) {
			this.id = id;
		}

		protected void upgradeLevel(User user) {
			if (user.getId().equals(this.id))
				throw new TestUserServiceException();// 지정된 id의 User 오브젝트가 발견되면 예외를 던져서 작업을 강제로 중단시킴.
			super.upgradeLevel(user);
		}

	}

	// 테스트용 예외
	static class TestUserServiceException extends RuntimeException {

	}

}
