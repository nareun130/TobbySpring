package springbook.user.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static springbook.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
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
	ApplicationContext context;

	@Autowired
	UserService userService;
	@Autowired
	UserService testUserService;// 같은 타입의 빈이 두개 존재 -> 필드 이름을 기준으로 주입될 빈이 결정됨.
	// 자동 프록시 생성기에 의해 트랜잭션 부가기능이 testUserService 빈에 적용됐는지를 확인하는 것이 목적

	@Autowired
	UserDao userDao;

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

	// MockUserDao를 사용해서 만든 고립된 테스트
	@Test
	public void upgradeLevels() throws Exception {
		UserServiceImpl userServiceImpl = new UserServiceImpl();

		MockUserDao mockUserDao = new MockUserDao(this.users);
		userServiceImpl.setUserDao(mockUserDao);

		MockMailSender mockMailSender = new MockMailSender();
		userServiceImpl.setMailSender(mockMailSender);

		userServiceImpl.upgradeLevels();

		List<User> updated = mockUserDao.getUpdated();
		assertThat(updated.size(), is(2));
		checkUserAndLevel(updated.get(0), "joytouch", Level.SILVER);
		checkUserAndLevel(updated.get(1), "madnite1", Level.GOLD);

		List<String> request = mockMailSender.getRequests();
		assertThat(request.size(), is(2));
		assertThat(request.get(0), is(users.get(1).getEmail()));
		assertThat(request.get(1), is(users.get(3).getEmail()));
	}

	private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
		assertThat(updated.getId(), is(expectedId));
		assertThat(updated.getLevel(), is(expectedLevel));
	}

	// 테스트용 UserDao 목오브젝트
	static class MockUserDao implements UserDao {
		private List<User> users;
		private List<User> updated = new ArrayList<User>();

		private MockUserDao(List<User> users) {
			this.users = users;
		}

		public List<User> getUpdated() {
			return this.updated;
		}

		@Override
		public void add(User user) {
			throw new UnsupportedOperationException();
		}

		@Override
		public User get(String id) {
			throw new UnsupportedOperationException();
		}

		@Override
		public List<User> getAll() {// 스텁 기능 제공
			return this.users;
		}

		@Override
		public void deleteAll() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int getCount() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void update(User user) {// 목 오브젝트 기능 제공
			updated.add(user);

		}
	}

	static class MockMailSender implements MailSender {
		// UserService로부터 전송 요청을 받은 메일 주소를 저장해두고 이를 읽을 수 있게 함.
		private List<String> requests = new ArrayList<String>();

		public List<String> getRequests() {
			return requests;
		}

		@Override
		public void send(SimpleMailMessage mailMessage) throws MailException {
			requests.add(mailMessage.getTo()[0]);// 전송 요청을 받은 이메일 주소를 저장해둠.
			// 간단하게 첫 번째 수신자 메일 주소만 저장
		}

		@Override
		public void send(SimpleMailMessage[] mailMessage) throws MailException {

		}

	}

	// Mockito를 적용한 테스트 코드
	@Test
	public void mockUpgradeLevels() {
		UserServiceImpl userServiceImpl = new UserServiceImpl();

		// 다이내믹한 목 오브젝트 생서과 메소드의 리턴 값 설정, DI까지
		UserDao mockUserDao = mock(UserDao.class);
		when(mockUserDao.getAll()).thenReturn(this.users);
		userServiceImpl.setUserDao(mockUserDao);

		// 리턴 값이 없는 메소드를 가진 목 오브젝트
		MailSender mockMailSender = mock(MailSender.class);
		userServiceImpl.setMailSender(mockMailSender);

		userServiceImpl.upgradeLevels();

		verify(mockUserDao, times(2)).update(any(User.class));
		verify(mockUserDao, times(2)).update(any(User.class));
		verify(mockUserDao).update(users.get(1));
		assertThat(users.get(1).getLevel(), is(Level.SILVER));
		verify(mockUserDao).update(users.get(3));
		assertThat(users.get(3).getLevel(), is(Level.GOLD));

		ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(mockMailSender, times(2)).send(mailMessageArg.capture());
		List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
		assertThat(mailMessages.get(0).getTo()[0], is(users.get(1).getEmail()));
		assertThat(mailMessages.get(1).getTo()[0], is(users.get(3).getEmail()));
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

	@Test // 스프링 컨텍스트의 빈 설정을 변경하지 않으므로 @DirtiesContext 애노테이션 제거. 모든 테스트를 위한 DI 작업은 설정파일을
			// 통해 서버에서 진행되므로 테스트 코드 자체는 단순해짐.
	public void upgradeAllOrNothing() throws Exception {

		userDao.deleteAll();
		for (User user : users)
			userDao.add(user);

		try {
			testUserService.upgradeLevels();
			fail("TestUserServiceException expected");// upgradeLevels가 정상적으로 종료되면 fail때문에 테스트가 실패할 것

		} catch (TestUserServiceException e) {

		}
		checkLevelUpgraded(users.get(1), false);
	}

	// 테스트용 서비스를 내부 클래스로 구현 -> 수정함.
	static class TestUserService extends UserServiceImpl {
		private String id = "madnite1"; // 테스트 픽스처의 users(3)의 id값을 고정시킴.

		protected void upgradeLevel(User user) {
			if (user.getId().equals(this.id))
				throw new TestUserServiceException();// 지정된 id의 User 오브젝트가 발견되면 예외를 던져서 작업을 강제로 중단시킴.
			super.upgradeLevel(user);
		}

		public List<User> getAll() {

			// 읽기 전용 트랜잭션 대상인 get으로 시작하는 메소드를 오버라이드한다.
			for (User user : super.getAll()) {
				// 강제로 쓰기를 시도. 여기서 읽기전용속성으로 인한 예외 발생해야함.
				super.update(user);
			}
			// 메소드가 끝나기 전에 예외가 발생해야 하니 리턴값은 별의미 없음
			return null;
		}
	}

	@Test(expected = TransientDataAccessResourceException.class)
	public void readOnlyTransactionAttribute() {
		testUserService.getAll();
	}

	// 테스트용 예외
	static class TestUserServiceException extends RuntimeException {

	}

}
