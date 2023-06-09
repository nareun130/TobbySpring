package springbook.user.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.exception.DuplicationIdException;
import springbook.user.sqlservice.SqlService;

@Repository("userDao")//userDao를 붙여주지 않으면 UserDaoTest가 실행되지 않는다.
public class UserDaoJdbc implements UserDao {

	@Autowired
	private SqlService sqlService;

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

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);

	}

	public void add(User user) {
		this.jdbcTemplate.update(this.sqlService.getSql("userAdd"), user.getId(), user.getName(), user.getPassword(),
				user.getEmail(), user.getLevel().intValue(), user.getLogin(), user.getRecommend());

	}

	public User get(String id) {
		return this.jdbcTemplate.queryForObject(this.sqlService.getSql("userGet"), new Object[] { id },
				this.userMapper);

	}

	public void deleteAll() {
		this.jdbcTemplate.update(this.sqlService.getSql("userDeleteAll"));
	}

	public int getCount() {
		return this.jdbcTemplate.queryForInt(this.sqlService.getSql("userGetCount"));
	}

	public List<User> getAll() {
		return this.jdbcTemplate.query(this.sqlService.getSql("userGetAll"), this.userMapper);
	}

	@Override
	public void update(User user) {
		this.jdbcTemplate.update(this.sqlService.getSql("userUpdate"), user.getName(), user.getPassword(),
				user.getEmail(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getId());
	}

}
