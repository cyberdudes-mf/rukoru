package hoshisugi.rukoru.app.services.repositorydb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;

import hoshisugi.rukoru.app.models.setings.RepositoryDBConnection;
import hoshisugi.rukoru.framework.database.Database;
import hoshisugi.rukoru.framework.inject.Injector;

class MariaDB implements AutoCloseable {

	private final Database database;
	private final Connection conn;

	public MariaDB() throws SQLException {
		database = Injector.getInstance(Database.class);
		final RepositoryDBConnection connection = RepositoryDBConnection.get();
		conn = DriverManager.getConnection(connection.getJdbcUrl(), connection.getUsername(), connection.getPassword());
	}

	public int executeUpdate(final String sql) throws SQLException {
		return database.executeUpdate(conn, sql);
	}

	public <T> List<T> executeQuery(final String sql, final Function<ResultSet, T> generator) throws SQLException {
		return database.executeQuery(conn, sql, generator);
	}

	@Override
	public void close() throws SQLException {
		conn.close();
	}
}
