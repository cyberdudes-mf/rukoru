package hoshisugi.rukoru.flamework.database;

import java.sql.SQLException;

public interface Database {

	int executeUpdate(String sql) throws SQLException;
}
