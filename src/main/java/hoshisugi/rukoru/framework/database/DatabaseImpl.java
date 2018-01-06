package hoshisugi.rukoru.framework.database;

import static hoshisugi.rukoru.framework.util.AssetUtil.loadSQL;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.google.common.util.concurrent.UncheckedExecutionException;

public class DatabaseImpl implements Database {

	static {
		try {
			Class.forName("org.h2.Driver");
		} catch (final ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public <T> List<T> executeQuery(final Function<ResultSet, T> generator, final String sql, final Object... params)
			throws SQLException {
		final ArrayList<T> result = new ArrayList<>();
		try (final Connection conn = DriverManager.getConnection(getJdbcUrl(), "sa", "")) {
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				for (int i = 0; i < params.length; i++) {
					stmt.setObject(i + 1, params[i]);
				}
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						result.add(generator.apply(rs));
					}
				}
			}
		}
		return result;
	}

	@Override
	public int executeUpdate(final String sql, final Object... params) throws SQLException {
		try (final Connection conn = DriverManager.getConnection(getJdbcUrl(), "sa", "")) {
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				for (int i = 0; i < params.length; i++) {
					stmt.setObject(i + 1, params[i]);
				}
				return stmt.executeUpdate();
			}
		}
	}

	private String getJdbcUrl() {
		final URL resource = getClass().getClassLoader().getResource(".");
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

	@Override
	public boolean exists(final String tableName) throws SQLException {
		return executeQuery(this::toBoolean, loadSQL("select_table_exist.sql"), tableName).get(0);
	}

	private Boolean toBoolean(final ResultSet rs) {
		try {
			return Boolean.valueOf(rs.getBoolean("result"));
		} catch (final SQLException e) {
			return Boolean.FALSE;
		}
	}
}
