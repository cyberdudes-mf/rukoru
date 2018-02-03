package hoshisugi.rukoru.framework.database.builder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UpdateBuilder extends QueryBuilder {

	private final Map<String, Object> set = new LinkedHashMap<>();
	private final Map<String, Object> where = new LinkedHashMap<>();

	private UpdateBuilder(final String table) {
		super(table);
	}

	public static UpdateBuilder table(final String table) {
		final UpdateBuilder builder = new UpdateBuilder(table);
		return builder;
	}

	public UpdateBuilder set(final Column... columns) {
		for (final Column column : columns) {
			set.put(column.getName(), column.getValue());
		}
		return this;
	}

	public UpdateBuilder where(final Column... columns) {
		for (final Column column : columns) {
			where.put(column.getName(), column.getValue());
		}
		return this;
	}

	@Override
	protected String getSql() {
		final StringBuilder sql = new StringBuilder();
		sql.append("update ").append(table).append(" set ")
				.append(String.join(",", set.keySet().stream().map(c -> c + " = ?").collect(Collectors.toList())));
		if (!where.isEmpty()) {
			sql.append(" where ").append(
					String.join(" and ", where.keySet().stream().map(c -> c + " = ?").collect(Collectors.toList())));
		}
		return sql.toString();
	}

	@Override
	protected void setParameters(final PreparedStatement stmt) throws SQLException {
		int num = 0;
		for (final Object value : set.values()) {
			stmt.setObject(++num, value);
		}
		for (final Object value : where.values()) {
			stmt.setObject(++num, value);
		}
	}
}
