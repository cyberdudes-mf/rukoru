package hoshisugi.rukoru.app.services.auth;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;

import com.google.common.util.concurrent.UncheckedExecutionException;

import hoshisugi.rukoru.framework.database.Database;
import hoshisugi.rukoru.framework.inject.Injector;

class H2Database implements AutoCloseable {

	private final Database database;
	private final Connection conn;

	public H2Database() throws SQLException {
		database = Injector.getInstance(Database.class);
		conn = DriverManager.getConnection(getJdbcUrl(), "sa", "");
	}

	private static String getJdbcUrl() {
		final URL resource = H2Database.class.getClassLoader().getResource(".");
		// REVISIT デバッグ実行と jar でやり方を同じにする
		Path binPath = null;
		if (resource != null) {
			try {
				binPath = Paths.get(resource.toURI());
			} catch (final URISyntaxException e) {
				throw new UncheckedExecutionException(e);
			}
		} else {
			binPath = Paths.get(".");
		}
		final Path databasePath = binPath.resolve("database");
		return String.format("jdbc:h2:%s", databasePath);
	}

	public int executeUpdate(final String sql, final Object... params) throws SQLException {
		return database.executeUpdate(conn, sql, params);
	}

	public <T> List<T> executeQuery(final String sql, final Function<ResultSet, T> generator, final Object... params)
			throws SQLException {
		return database.executeQuery(conn, sql, generator, params);
	}

	public boolean exists(final String tableName) throws SQLException {
		return database.exists(conn, tableName);
	}

	@Override
	public void close() throws SQLException {
		conn.close();
	}

}
