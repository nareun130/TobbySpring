package springbook;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.metadata.GenericTableMetaDataProvider;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mysql.cj.jdbc.Driver;

import springbook.user.service.DummyMailSender;
import springbook.user.service.UserService;
import springbook.user.service.UserServiceTest.TestUserService;

//DO 메타정보로 사용될 TestApplicationContext클래스
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "springbook.user")
@Import(SqlServiceContext.class)
@PropertySource("/database.properties")
public class AppContext {

//	@Autowired
//	Environment env;

	@Value("${db.driverClass}")
	Class<? extends Driver> driverClass;
	@Value("${db.url}")
	String url;
	@Value("${db.username}")
	String username;
	@Value("${db.password}")
	String password;

	// DB 연결 & 트랜잭션
	@Bean
	public DataSource dataSource() {

		SimpleDriverDataSource ds = new SimpleDriverDataSource();
		ds.setDriverClass(this.driverClass);
		ds.setUrl(this.url);
		ds.setUsername(this.username);
		ds.setPassword(this.password);
		return ds;
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		DataSourceTransactionManager tm = new DataSourceTransactionManager();
		tm.setDataSource(dataSource());
		return tm;
	}

	@Configuration
	@Profile("production")
	public static class ProductionAppContext {
		@Bean
		public MailSender mailSender() {
			JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
			mailSender.setHost("mail.mycompony.com");
			return mailSender;
		}

	}

	@Configuration
	@Profile("test")
	public static class TestAppContext {

		@Bean
		public UserService testUserService() {
			return new TestUserService();
		}

		@Bean
		public MailSender mailSender() {
			return new DummyMailSender();
		}
	}

}
