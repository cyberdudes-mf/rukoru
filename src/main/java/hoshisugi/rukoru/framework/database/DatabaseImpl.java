package hoshisugi.rukoru.framework.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import hoshisugi.rukoru.framework.database.builder.CreateBuilder;
import hoshisugi.rukoru.framework.database.builder.DeleteBuilder;
import hoshisugi.rukoru.framework.database.builder.DropBuilder;
import hoshisugi.rukoru.framework.database.builder.InsertBuilder;
import hoshisugi.rukoru.framework.database.builder.QueryBuilder;
import hoshisugi.rukoru.framework.database.builder.SelectBuilder;
import hoshisugi.rukoru.framework.database.builder.UpdateBuilder;

public class DatabaseImpl implements Database {

	@Override
	public <T> List<T> select(final Connection conn, final SelectBuilder build, final Function<ResultSet, T> generator)
			throws SQLException {
		final ArrayList<T> result = new ArrayList<>();
		try (PreparedStatement stmt = build.build(conn); ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				final T t = generator.apply(rs);
				if (t != null) {
					result.add(generator.apply(rs));
				}
			}
		}
		return result;
	}

	@Override
	public int insert(final Connection conn, final InsertBuilder builder) throws SQLException {
		return execute(conn, builder);
	}

	@Override
	public int update(final Connection conn, final UpdateBuilder builder) throws SQLException {
		return execute(conn, builder);
	}

	@Override
	public int delete(final Connection conn, final DeleteBuilder builder) throws SQLException {
		return execute(conn, builder);
	}

	@Override
	public int create(final Connection conn, final CreateBuilder builder) throws SQLException {
		return execute(conn, builder);
	}

	@Override
	public int drop(final Connection conn, final DropBuilder builder) throws SQLException {
		return execute(conn, builder);
	}

	private int execute(final Connection conn, final QueryBuilder builder) throws SQLException {
		try (PreparedStatement stmt = builder.build(conn)) {
			return stmt.executeUpdate();
		}
	}

}
