package hoshisugi.rukoru.flamework.database;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.google.common.util.concurrent.UncheckedExecutionException;

import hoshisugi.rukoru.flamework.inject.Injectable;

public class DatabaseImpl implements Database, Injectable {

	static {
		try {
			Class.forName("org.h2.Driver");
		} catch (final ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public int executeUpdate(final String sql) throws SQLException {
		try (final Connection conn = DriverManager.getConnection(getJdbcUrl(), "sa", "")) {
			final Statement stmt = conn.createStatement();
			return stmt.executeUpdate(sql);
		}
	}

	private String getJdbcUrl() {
		try {
			final Path binPath = Paths.get(getClass().getClassLoader().getResource(".").toURI());
			final Path databasePath = binPath.resolve("database");
			return String.format("jdbc:h2:%s", databasePath);
		} catch (final URISyntaxException e) {
			throw new UncheckedExecutionException(e);
		}
	}
}
