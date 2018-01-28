package hoshisugi.rukoru.framework.util;

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
		return new SelectBuilder(AssetUtil.loadSQL(String.format("select_%s.sql", table)));
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

	public String getSql() {
		return sql.toString();
	}

	public void setParameter(final PreparedStatement statement) throws SQLException {
		for (int i = 0; i < params.size(); i++) {
			statement.setObject(i + 1, params.get(i));
		}
	}
}
