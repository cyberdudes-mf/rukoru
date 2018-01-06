package hoshisugi.rukoru.framework.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;

import hoshisugi.rukoru.framework.inject.Injectable;

public interface Database extends Injectable {

	int executeUpdate(String sql, Object... params) throws SQLException;

	<T> List<T> executeQuery(Function<ResultSet, T> generator, String sql, Object... params) throws SQLException;

	boolean exists(String tableName) throws SQLException;
}
