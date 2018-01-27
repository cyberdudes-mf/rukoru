package hoshisugi.rukoru.app.services.repositorydb;

import static hoshisugi.rukoru.framework.util.AssetUtil.loadSQL;

import java.sql.SQLException;
import java.util.List;

import hoshisugi.rukoru.app.models.repositorydb.RepositoryDB;
import hoshisugi.rukoru.framework.base.BaseService;

public class RepositoryDBServiceImpl extends BaseService implements RepositoryDBService {

	@Override
	public void dropRepositoryDB(final String dbName) throws SQLException {
		try (MariaDB db = new MariaDB()) {
			db.executeUpdate("drop database", dbName);
		}
	}

	@Override
	public void createRepositoryDB(final String dbName) throws SQLException {
		try (MariaDB db = new MariaDB()) {
			db.executeUpdate("create database", dbName);
		}
	}

	@Override
	public List<RepositoryDB> listRepositoryDB() throws SQLException {
		try (MariaDB db = new MariaDB()) {
			return db.executeQuery(loadSQL("show_repositorydb_list.sql"), RepositoryDB::new);
		}
	}

}
