package hoshisugi.rukoru.framework.database.builder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DeleteBuilder extends QueryBuilder {

	private final Map<String, Object> where = new LinkedHashMap<>();

	private DeleteBuilder(final String table) {
		super(table);
	}

	public static DeleteBuilder from(final String table) {
		final DeleteBuilder builder = new DeleteBuilder(table);
		return builder;
	}

	public DeleteBuilder where(final Column... columns) {
		for (final Column column : columns) {
			where.put(column.getName(), column.getValue());
		}
		return this;
	}

	@Override
	protected String getSql() {
		final StringBuilder sql = new StringBuilder();
		sql.append("delete from ").append(table);
		if (!where.isEmpty()) {
			sql.append(" where ").append(
					String.join(" and ", where.keySet().stream().map(c -> c + " = ?").collect(Collectors.toList())));
		}
		return sql.toString();
	}

	@Override
	protected void setParameters(final PreparedStatement stmt) throws SQLException {
		int num = 0;
		for (final Object value : where.values()) {
			stmt.setObject(++num, value);
		}
	}
}
