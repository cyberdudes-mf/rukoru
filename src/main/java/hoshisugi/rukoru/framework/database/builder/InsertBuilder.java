package hoshisugi.rukoru.framework.database.builder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InsertBuilder extends QueryBuilder {

	private final Map<String, Object> values = new LinkedHashMap<>();
	private final Map<String, Object> where = new LinkedHashMap<>();

	private InsertBuilder(final String table) {
		super(table);
	}

	public static InsertBuilder into(final String table) {
		final InsertBuilder builder = new InsertBuilder(table);
		return builder;
	}

	public InsertBuilder values(final Column... columns) {
		for (final Column column : columns) {
			values.put(column.getName(), column.getValue());
		}
		return this;
	}

	public InsertBuilder where(final Column... columns) {
		for (final Column column : columns) {
			where.put(column.getName(), column.getValue());
		}
		return this;
	}

	@Override
	protected String getSql() {
		final StringBuilder sql = new StringBuilder();
		sql.append("insert into ").append(table).append(" (").append(String.join(",", values.keySet()))
				.append(") values (")
				.append(String.join(",",
						IntStream.rangeClosed(1, values.size()).mapToObj(i -> "?").collect(Collectors.toList())))
				.append(")");
		if (!where.isEmpty()) {
			sql.append(" where ").append(
					String.join(" and ", where.keySet().stream().map(c -> c + " = ?").collect(Collectors.toList())));
		}
		return sql.toString();
	}

	@Override
	protected void setParameters(final PreparedStatement stmt) throws SQLException {
		int num = 0;
		for (final Object value : values.values()) {
			stmt.setObject(++num, value);
		}
		for (final Object value : where.values()) {
			stmt.setObject(++num, value);
		}
	}
}
