package springbook.user.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.mysql.cj.exceptions.MysqlErrorNumbers;

import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.exception.DuplicationIdException;

public class UserDaoJdbc implements UserDao {

	private JdbcTemplate jdbcTemplate;

	// 인스턴스 변수userMapper에 콜백 오브젝트를 저장
	private RowMapper<User> userMapper = new RowMapper<User>() {

		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
			user.setEmail(rs.getString("email"));
			user.setLevel(Level.valueOf(rs.getInt("level")));
			user.setLogin(rs.getInt("login"));
			user.setRecommend(rs.getInt("recommend"));
			return user;
		}
	};

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);

	}

	public void add(User user) {
		this.jdbcTemplate.update(
				"insert into users(id, name, password, email, level, login, recommend) values(?,?,?,?,?,?,?)",
				user.getId(), user.getName(), user.getPassword(), user.getEmail(), user.getLevel().intValue(),
				user.getLogin(), user.getRecommend());

	}

	public User get(String id) {
		return this.jdbcTemplate.queryForObject("select * from users where id = ?", new Object[] { id },
				this.userMapper);

	}

	public void deleteAll() {
		this.jdbcTemplate.update("delete from users");
	}

	public int getCount() {
		return this.jdbcTemplate.queryForInt("select count(*) from users");
	}

	public List<User> getAll() {
		return this.jdbcTemplate.query("select * from users order by id", this.userMapper);
	}

	@Override
	public void update(User user) {
		this.jdbcTemplate.update(
				"update users set name = ?, password = ?, email = ?, level = ?, login = ?,"
						+ "recommend = ? where id = ?",
				user.getName(), user.getPassword(), user.getEmail(), user.getLevel().intValue(), user.getLogin(),
				user.getRecommend(), user.getId());
	}

}
