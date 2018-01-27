package hoshisugi.rukoru.app.services.repositorydb;

import java.sql.SQLException;
import java.util.List;

import hoshisugi.rukoru.app.models.repositorydb.RepositoryDB;

public interface RepositoryDBService {
	void dropRepositoryDB(String dbName) throws SQLException;

	void createRepositoryDB(String dbName) throws SQLException;

	List<RepositoryDB> listRepositoryDB() throws SQLException;
}
