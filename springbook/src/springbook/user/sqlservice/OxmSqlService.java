package springbook.user.sqlservice;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.springframework.oxm.Unmarshaller;

import springbook.user.dao.UserDao;
import springbook.user.sqlservice.jaxb.SqlType;
import springbook.user.sqlservice.jaxb.Sqlmap;

public class OxmSqlService implements SqlService {

	// SqlService의 실제 구현 부분을 위임할 대상인 BaseSqlService를 인스턴스 변수로 정의해둠.
	private final BaseSqlService baseSqlService = new BaseSqlService();
	private final OxmSqlReader oxmSqlReader = new OxmSqlReader();

	// oxmSqlReader와 달리 단지 디폴트 오브젝트로 만들어진 프로퍼티 -> 필요에 따라 DI를 통해 교체 가능
	private SqlRegistry sqlRegistry = new HashMapSqlRegistry();

	public void setSqlRegistry(SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
	}

	public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.oxmSqlReader.setUnmarshaller(unmarshaller);
	}

	public void setSqlmapFile(String sqlmapFile) {
		this.oxmSqlReader.setSqlmapFile(sqlmapFile);
	}

	@PostConstruct
	public void loadSql() {
		// OxmSqlService의 프로퍼티를 통해서 초기화된 sqlReader와 sqlRegistry를 실제 작업을 위임할 대상인
		// baseSqlService에 주입.
		this.baseSqlService.setSqlReader(this.oxmSqlReader);
		this.baseSqlService.setSqlRegistry(this.sqlRegistry);

		this.baseSqlService.loadSql(); // SQL을 등록하는 초기화 작업을 baseSqlService에 위임.
	}

	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
		return this.baseSqlService.getSql(key);
	}

	private class OxmSqlReader implements SqlReader {

		private Unmarshaller unmarshaller;
		private final static String DEFAULT_SQLMAP_FILE = "sqlmap.xml";
		private String sqlmapFile = DEFAULT_SQLMAP_FILE;

		public void setUnmarshaller(Unmarshaller unmarshaller) {
			this.unmarshaller = unmarshaller;
		}

		public void setSqlmapFile(String sqlmapFile) {
			this.sqlmapFile = sqlmapFile;
		}

		@Override
		public void read(SqlRegistry registry) {
			try {

				Source source = new StreamSource(UserDao.class.getResourceAsStream(this.sqlmapFile));
				// OxmSqlService를 통해 전달받은 OXM 인터페이스 구현 오브젝트를 가지고 언마샬링 작업 수행
				Sqlmap sqlmap = (Sqlmap) this.unmarshaller.unmarshal(source);

				for (SqlType sql : sqlmap.getSql()) {
					sqlRegistry.registerSql(sql.getKey(), sql.getValue());
				}

			} catch (IOException e) {
				throw new IllegalArgumentException(this.sqlmapFile + "을 가져올 수 없습니다.", e);
			}
		}

	}

}
