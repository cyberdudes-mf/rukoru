package hoshisugi.rukoru.app.services.repositorydb;

import static hoshisugi.rukoru.framework.util.SelectBuilder.query;
import static java.util.Arrays.asList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.google.common.util.concurrent.UncheckedExecutionException;

import hoshisugi.rukoru.app.models.repositorydb.RepositoryDB;
import hoshisugi.rukoru.framework.base.BaseService;

public class RepositoryDBServiceImpl extends BaseService implements RepositoryDBService {

	private static final List<String> EXCLUDED_SCHEMA = asList("information_schema", "innodb", "mysql", "tmp",
			"performance_schema");

	@Override
	public void dropRepositoryDB(final String dbName) throws SQLException {
		try (MariaDB db = new MariaDB()) {
			db.executeUpdate("drop database " + dbName);
		}
	}

	@Override
	public RepositoryDB createRepositoryDB(final String dbName) throws SQLException {
		try (MariaDB db = new MariaDB()) {
			db.executeUpdate("create database " + dbName);
		}
		return new RepositoryDB(dbName);
	}

	@Override
	public List<RepositoryDB> listRepositoryDB() throws SQLException {
		try (MariaDB db = new MariaDB()) {
			return db.executeQuery(query("show databases"), this::createModel);
		}
	}

	private RepositoryDB createModel(final ResultSet rs) {
		try {
			return EXCLUDED_SCHEMA.contains(rs.getString(1)) ? null : new RepositoryDB(rs);
		} catch (final SQLException e) {
			throw new UncheckedExecutionException(e);
		}
	}
}
