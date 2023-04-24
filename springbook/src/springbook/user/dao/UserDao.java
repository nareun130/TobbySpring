package springbook.user.dao;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;

import springbook.user.domain.User;

public class UserDao {

	private DataSource dataSource;
	private JdbcContext jdbcContext;

	public void setDataSource(DataSource dataSource) {
		this.jdbcContext = new JdbcContext(); // jdbcContext를 생성
		this.jdbcContext.setDataSource(dataSource); // 외존 오브젝트 주입
		this.dataSource = dataSource;// 아직 jdbcContext를 적용하지 않은 메소드를 위해 저장해 둠.
	}

	public void add(final User user) throws SQLException, ClassNotFoundException {

		// 익명 내부 클래스를 메소드 파라미터로 이전
		this.jdbcContext.executeSql("insert into users(id,name,password) values(?,?,?)", user.getId(), user.getName(),
				user.getPassword());
	}

	public User get(String id) throws ClassNotFoundException, SQLException {
		Connection c = dataSource.getConnection();

		PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
		ps.setString(1, id);

		ResultSet rs = ps.executeQuery();

		User user = null;
		// getUserFailure를 위한 코드 수정
		if (rs.next()) {
			user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
		}

		rs.close();
		ps.close();
		c.close();

		if (user == null)
			throw new EmptyResultDataAccessException(1);
		return user;
	}

	public void deleteAll() throws SQLException {
		this.jdbcContext.executeSql("delete from users");
	}

	public int getCount() throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			c = dataSource.getConnection();
			ps = c.prepareStatement("select count(*) from users");
			rs = ps.executeQuery();
			rs.next();
			return rs.getInt(1);

		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
			if (c != null) {
				try {
					c.close();
				} catch (SQLException e) {
				}
			}
		}

	}

}
