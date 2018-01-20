package hoshisugi.rukoru.framework.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;

import hoshisugi.rukoru.framework.inject.Injectable;

public interface Database extends Injectable {

	int executeUpdate(Connection conn, String sql, Object... params) throws SQLException;

	<T> List<T> executeQuery(Connection conn, String sql, Function<ResultSet, T> generator, Object... params)
			throws SQLException;

	boolean exists(Connection conn, String tableName) throws SQLException;

}
