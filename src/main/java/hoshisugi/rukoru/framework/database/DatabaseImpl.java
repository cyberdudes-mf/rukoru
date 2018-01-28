package hoshisugi.rukoru.framework.database;

import static hoshisugi.rukoru.framework.util.SelectBuilder.from;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import hoshisugi.rukoru.framework.util.SelectBuilder;

public class DatabaseImpl implements Database {

	@Override
	public <T> List<T> executeQuery(final Connection conn, final SelectBuilder sql,
			final Function<ResultSet, T> generator) throws SQLException {
		final ArrayList<T> result = new ArrayList<>();
		try (PreparedStatement stmt = conn.prepareStatement(sql.getSql())) {
			sql.setParameter(stmt);
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
		final SelectBuilder select = from("information_schema.tables").where("table_schema", "PUBLIC").and("table_name",
				tableName);
		return executeQuery(conn, select, this::toBoolean).get(0);
	}

	private Boolean toBoolean(final ResultSet rs) {
		try {
			return Boolean.valueOf(rs.getBoolean("result"));
		} catch (final SQLException e) {
			return Boolean.FALSE;
		}
	}
}
