package hoshisugi.rukoru.framework.database.builder;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DropBuilder extends QueryBuilder {

	private DropBuilder(final String table) {
		super(table);
	}

	public static DropBuilder table(final String table) {
		final DropBuilder builder = new DropBuilder(table);
		return builder;
	}

	@Override
	protected String getSql() {
		return "drop table " + table;
	}

	@Override
	protected void setParameters(final PreparedStatement stmt) throws SQLException {
	}

}
