package hoshisugi.rukoru.framework.database.builder;

import static hoshisugi.rukoru.framework.util.AssetUtil.loadSQL;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreateBuilder extends QueryBuilder {

	private final StringBuilder sql = new StringBuilder();

	private CreateBuilder(final String table) {
		super(null);
		sql.append(loadSQL(String.format("create_%s.sql", table)));
	}

	public static CreateBuilder table(final String table) {
		final CreateBuilder builder = new CreateBuilder(table);
		return builder;
	}

	@Override
	protected String getSql() {
		return sql.toString();
	}

	@Override
	protected void setParameters(final PreparedStatement stmt) throws SQLException {
	}

}
