package hoshisugi.rukoru.app.services.repositorydb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.function.Function;

import hoshisugi.rukoru.framework.database.Database;
import hoshisugi.rukoru.framework.inject.Injector;

class MariaDB implements AutoCloseable {
	private final String jdbcUrl = "jdbc:mariadb://repositorydb-mariadb.cis3jmdkfkbo.ap-northeast-1.rds.amazonaws.com:3306";

	private final Database database;
	private final Connection conn;

	public MariaDB() throws SQLException {
		database = Injector.getInstance(Database.class);
		conn = DriverManager.getConnection(jdbcUrl, "", "");
	}

	public int executeUpdate(final String sql, final String dbName) throws SQLException {
		final Statement stmt = conn.createStatement();
		return stmt.executeUpdate(sql + " " + dbName);
	}

	public <T> List<T> executeQuery(final String sql, final Function<ResultSet, T> generator) throws SQLException {
		return database.executeQuery(conn, sql, generator);
	}

	@Override
	public void close() throws SQLException {
		conn.close();
	}
}
