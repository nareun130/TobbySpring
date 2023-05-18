package springbook.learningtest.spring.embeddeddb;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

public class EmbeddedDbTest {
	EmbeddedDatabase db;
	SimpleJdbcTemplate template; // JdbcTemplate을 더 편리하게 사용할 수 있게 확장한 템플릿

	@Before
	public void setUp() {
		db = new EmbeddedDatabaseBuilder().setType(HSQL)// HSQL, DERBY, H2 세가지 중 하나 선택가능. 초기화 SQL이 호환가능 하면 DB 종류는 언제든 바뀔
														// 수 있음.
				.addScript("classpath:/springbook/learningtest/spring/embeddeddb/schema.sql")
				.addScript("classpath:/springbook/learningtest/spring/embeddeddb/data.sql").build();

		template = new SimpleJdbcTemplate(db);// EmbeddedDatabase는 DataSource의 서브 인터페이스 -> DataSource를 필요로 하는
												// SimpleJdbcTemplate을 만들 때 사용 가능
	}

	@After
	public void tearDown() {// 매 테스트를 진행한 뒤에 DB 종료.
		// 내장형 메모리 DB는 따로 저장하지 않는 한 애플리케이션과 함께 매번 새롭게 DB가 만들어지고 제거되는 생명주기를 가짐.

		db.shutdown();
	}

	@Test
	public void initData() {// 초기화 스크립트를 통해 등록된 데이터를 검증하는 테스트
		assertThat(template.queryForInt("select count(*) from sqlmap"), is(2));

		List<Map<String, Object>> list = template.queryForList("select * from sqlmap order by key_");
		assertThat((String) list.get(0).get("key_"), is("KEY1"));
		assertThat((String) list.get(0).get("sql_"), is("SQL1"));
		assertThat((String) list.get(1).get("key_"), is("KEY2"));
		assertThat((String) list.get(1).get("sql_"), is("SQL2"));
		
	}

	@Test
	public void insert() {
		template.update("insert into sqlmap(key_,sql_) values(?,?)", "KEY3", "SQL3");

		assertThat(template.queryForInt("select count(*) from sqlmap"), is(3));
	}

}
