package hoshisugi.rukoru.framework.database;

import static hoshisugi.rukoru.framework.util.AssetUtil.loadSQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DatabaseImpl implements Database {

	@Override
	public <T> List<T> executeQuery(final Connection conn, final String sql, final Function<ResultSet, T> generator,
			final Object... params) throws SQLException {
		final ArrayList<T> result = new ArrayList<>();
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			for (int i = 0; i < params.length; i++) {
				stmt.setObject(i + 1, params[i]);
			}
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					final T t = generator.apply(rs);
					if (t != null) {
						result.add(generator.apply(rs));
					}
				}
			}
		}
		return result;
	}

	@Override
	public int executeUpdate(final Connection conn, final String sql, final Object... params) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			for (int i = 0; i < params.length; i++) {
				stmt.setObject(i + 1, params[i]);
			}
			return stmt.executeUpdate();
		}
	}

	@Override
	public boolean exists(final Connection conn, final String tableName) throws SQLException {
		return executeQuery(conn, loadSQL("select_table_exist.sql"), this::toBoolean, tableName).get(0);
	}

	private Boolean toBoolean(final ResultSet rs) {
		try {
			return Boolean.valueOf(rs.getBoolean("result"));
		} catch (final SQLException e) {
			return Boolean.FALSE;
		}
	}
}
