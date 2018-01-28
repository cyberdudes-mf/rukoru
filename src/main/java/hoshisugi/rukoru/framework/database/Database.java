package hoshisugi.rukoru.framework.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;

import hoshisugi.rukoru.framework.inject.Injectable;
import hoshisugi.rukoru.framework.util.SelectBuilder;

public interface Database extends Injectable {

	int executeUpdate(Connection conn, String sql, Object... params) throws SQLException;

	<T> List<T> executeQuery(Connection conn, SelectBuilder select, Function<ResultSet, T> generator)
			throws SQLException;

	boolean exists(Connection conn, String tableName) throws SQLException;

}
