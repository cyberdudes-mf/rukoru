package hoshisugi.rukoru.app.services.settings;

import static hoshisugi.rukoru.framework.database.builder.Column.$;
import static hoshisugi.rukoru.framework.database.builder.SelectBuilder.from;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.google.common.util.concurrent.UncheckedExecutionException;

import hoshisugi.rukoru.framework.database.Database;
import hoshisugi.rukoru.framework.database.builder.CreateBuilder;
import hoshisugi.rukoru.framework.database.builder.DeleteBuilder;
import hoshisugi.rukoru.framework.database.builder.InsertBuilder;
import hoshisugi.rukoru.framework.database.builder.SelectBuilder;
import hoshisugi.rukoru.framework.database.builder.UpdateBuilder;
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
		final Path databasePath = getBinPath(resource).resolve("database");
		return String.format("jdbc:h2:%s", databasePath);
	}

	private static Path getBinPath(final URL resource) {
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
		return binPath;
	}

	public int create(final CreateBuilder builder) throws SQLException {
		return database.create(conn, builder);
	}

	public <T> List<T> select(final SelectBuilder builder, final Function<ResultSet, T> generator) throws SQLException {
		return database.select(conn, builder, generator);
	}

	public <T> Optional<T> find(final SelectBuilder builder, final Function<ResultSet, T> generator)
			throws SQLException {
		return database.find(conn, builder, generator);
	}

	public int insert(final InsertBuilder builder) throws SQLException {
		return database.insert(conn, builder);
	}

	public int update(final UpdateBuilder builder) throws SQLException {
		return database.update(conn, builder);
	}

	public int delete(final DeleteBuilder builder) throws SQLException {
		return database.delete(conn, builder);
	}

	public boolean exists(final String tableName) throws SQLException {
		final SelectBuilder select = from("information_schema.tables").where($("table_schema", "PUBLIC"),
				$("table_name", tableName));
		return database.find(conn, select, this::toBoolean).get();
	}

	private Boolean toBoolean(final ResultSet rs) {
		try {
			return Boolean.valueOf(rs.getBoolean("result"));
		} catch (final SQLException e) {
			return Boolean.FALSE;
		}
	}

	@Override
	public void close() throws SQLException {
		conn.close();
	}

}
