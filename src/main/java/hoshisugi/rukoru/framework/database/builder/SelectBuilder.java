package hoshisugi.rukoru.framework.database.builder;

import static hoshisugi.rukoru.framework.util.AssetUtil.loadSQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SelectBuilder {

	private final StringBuilder sql = new StringBuilder();

	private final List<Object> params = new ArrayList<>();

	private SelectBuilder(final String query) {
		sql.append(query);
	}

	public static SelectBuilder query(final String query) {
		return new SelectBuilder(query);
	}

	public static SelectBuilder from(final String table) {
		return new SelectBuilder(loadSQL(String.format("select_%s.sql", table)));
	}

	public SelectBuilder where(final String column, final Object value) {
		sql.append(" where ").append(column).append(" = ?");
		params.add(value);
		return this;
	}

	public SelectBuilder and(final String column, final Object value) {
		sql.append(" and ").append(column).append(" = ?");
		params.add(value);
		return this;
	}

	public PreparedStatement build(final Connection conn) throws SQLException {
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(getSql());
			setParameters(stmt);
		} catch (final SQLException e) {
			if (stmt != null) {
				stmt.close();
			}
			throw e;
		}
		return stmt;
	}

	private String getSql() {
		return sql.toString();
	}

	private void setParameters(final PreparedStatement statement) throws SQLException {
		for (int i = 0; i < params.size(); i++) {
			statement.setObject(i + 1, params.get(i));
		}
	}
}
