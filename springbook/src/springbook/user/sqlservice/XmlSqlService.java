package springbook.user.sqlservice;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import springbook.user.dao.UserDao;
import springbook.user.sqlservice.jaxb.SqlType;
import springbook.user.sqlservice.jaxb.Sqlmap;

public class XmlSqlService implements SqlService, SqlRegistry, SqlReader {

	// -----------SqlProvicer--------------
	// 구현한 코드는 XmlSqlService클래스 자신이지만, 인터페이스를 통해 접근하도록 인터페이스에 의존적으로 만든다.
	private SqlReader sqlReader;
	private SqlRegistry sqlRegistry;

	public void setSqlReader(SqlReader sqlReader) {
		this.sqlReader = sqlReader;
	}

	public void setSqlRegistry(SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
	}

	// 생성자 대신 사용할 초기화 메소드
	@PostConstruct
	public void loadSql() {
		this.sqlReader.read(this.sqlRegistry);
	}

	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
		try {
			return this.sqlRegistry.findSql(key);
		} catch (SqlNotFoundException e) {
			throw new SqlRetrievalFailureException(e);
		}
	}

	// -----------SqlRegistry--------------
	private Map<String, String> sqlMap = new HashMap<String, String>();

	@Override
	public void registerSql(String key, String sql) {
		// HashMap이라는 저장소를 사용하는 구체적인 구현 방법에서 독립될 수 있도록 인터페이스의 메소드로 접근하게 해줌.
		sqlMap.put(key, sql);
	}

	@Override
	public String findSql(String key) throws SqlNotFoundException {
		String sql = sqlMap.get(key);
		if (sql == null)
			throw new SqlRetrievalFailureException(key + "를 이용해서 SQL을 찾을 수 없습니다.");
		else
			return sql;
	}

	// -----------SqlReader--------------
	private String sqlmapFile;

	// sqlmapFile은 SqlReader구현의 일부가 됨. -> SqlReader의 구현 메소드를 통하지 않고 접근하면 안됨.
	public void setSqlmapFile(String sqlmapFile) {
		this.sqlmapFile = sqlmapFile;
	}

	@Override
	public void read(SqlRegistry registry) {
		String contextPath = Sqlmap.class.getPackage().getName();
		try {
			JAXBContext context = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			InputStream is = UserDao.class.getResourceAsStream("sqlmap.xml");
			Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(is);

			for (SqlType sql : sqlmap.getSql()) {
				// SQL 저장 로직 구현에 독립적인 인터페이스 메소드를 통해 읽어들인 SQL과 키를 전달.
				sqlRegistry.registerSql(sql.getKey(), sql.getValue());
			}
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

}
