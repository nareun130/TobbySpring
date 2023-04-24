package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

public class JdbcContext {
	DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void workWithStatementStrategy(StatementStrategy stmt) throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = dataSource.getConnection();
			ps = stmt.makePreparedStatemnet(c);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
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

	public void executeSql(final String... query) throws SQLException {
		workWithStatementStrategy(new StatementStrategy() {

			@Override
			public PreparedStatement makePreparedStatemnet(Connection c) throws SQLException {
				if (query.length == 1) {
					return c.prepareStatement(query[0]);
				} else {

					PreparedStatement ps = c.prepareStatement(query[0]);
					for (int i = 1; i < query.length; i++) {
						ps.setString(i, query[i]);
					}

					return ps;
				}

			}
		});
	}
}
