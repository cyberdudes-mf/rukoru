package hoshisugi.rukoru.app.services.repositorydb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;

import hoshisugi.rukoru.app.models.settings.RepositoryDBConnection;
import hoshisugi.rukoru.framework.database.Database;
import hoshisugi.rukoru.framework.database.builder.SelectBuilder;
import hoshisugi.rukoru.framework.inject.Injector;

class MariaDB implements AutoCloseable {

	private final Database database;
	private final Connection conn;

	public MariaDB() throws SQLException {
		database = Injector.getInstance(Database.class);
		final RepositoryDBConnection connection = RepositoryDBConnection.get();
		conn = DriverManager.getConnection(connection.getJdbcUrl(), connection.getUsername(), connection.getPassword());
	}

	public int createDatabase(final String dbName) throws SQLException {
		return database.executeUpdate(conn, "create database " + dbName);
	}

	public int dropDatabase(final String dbName) throws SQLException {
		return database.executeUpdate(conn, "drop database " + dbName);
	}

	public <T> List<T> select(final SelectBuilder builder, final Function<ResultSet, T> generator) throws SQLException {
		return database.select(conn, builder, generator);
	}

	@Override
	public void close() throws SQLException {
		conn.close();
	}
}
