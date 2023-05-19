package springbook.user.sqlservice.updatable;

import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import static org.junit.Assert.fail;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Test;

import springbook.user.sqlservice.SqlUpdateFailureException;
import springbook.user.sqlservice.UpdatableSqlRegistry;

public class EmbeddedDbSqlRegistryTest extends AbstractUpdatableSqlRegistryTest {

	EmbeddedDatabase db;

	@Test
	public void transactionalUpdate() {
		// 초기 상태를 확인. -> 트랜잭션 롤백 후의 결과와 비교되서 이 테스트의 목적인 롤백 후의 상태는 처음과 동일하다는 것을 보여주려고 넣음.
		checkFind("SQL1", "SQL2", "SQL3");
		Map<String, String> sqlmap = new HashMap<String, String>();
		sqlmap.put("KEY1", "modified1");
		sqlmap.put("KEY9999!@#$", "Modified9999");

		try {
			sqlRegistry.updateSql(sqlmap);
			fail();// 예외가 발생해서 catch로 넘어가지 않으면 뭔가 잘못된 것.-> fail()로 강제 실패 발생 시키고 원인을 찾아야 한다.
		} catch (SqlUpdateFailureException e) {
		}
		// 첫 번째 SQL은 정상적으로 수정 했지만 트랜잭션이 롤백되기 때문에 다시 변경 이전 상태로 돌아와야 함.
		// -> 트랜잭션이 적용되지 않는다면 변경된 채로 남아서 테스트는 실패할 것
		checkFind("SQL1", "SQL2", "SQL3");

	}

	@Override
	protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
		db = new EmbeddedDatabaseBuilder().setType(HSQL)
				.addScript("classpath:springbook/user/sqlservice/updatable/sqlRegistrySchema.sql").build();

		EmbeddedDbSqlRegistry embeddedDbSqlRegistry = new EmbeddedDbSqlRegistry();
		embeddedDbSqlRegistry.setDataSource(db);
		return embeddedDbSqlRegistry;
	}

	@After
	public void tearDown() {
		db.shutdown();
	}
}
