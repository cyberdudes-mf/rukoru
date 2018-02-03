package hoshisugi.rukoru.framework.database.builder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class QueryBuilder {

	protected final String table;

	protected QueryBuilder(final String table) {
		this.table = table;
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

	protected abstract String getSql();

	protected abstract void setParameters(final PreparedStatement stmt) throws SQLException;
}
