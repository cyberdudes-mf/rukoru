package hoshisugi.rukoru.framework.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import hoshisugi.rukoru.framework.database.builder.CreateBuilder;
import hoshisugi.rukoru.framework.database.builder.DeleteBuilder;
import hoshisugi.rukoru.framework.database.builder.DropBuilder;
import hoshisugi.rukoru.framework.database.builder.InsertBuilder;
import hoshisugi.rukoru.framework.database.builder.SelectBuilder;
import hoshisugi.rukoru.framework.database.builder.UpdateBuilder;
import hoshisugi.rukoru.framework.inject.Injectable;

public interface Database extends Injectable {

	<T> List<T> select(Connection conn, SelectBuilder builder, Function<ResultSet, T> generator) throws SQLException;

	<T> Optional<T> find(Connection conn, SelectBuilder builder, Function<ResultSet, T> generator) throws SQLException;

	int insert(Connection conn, InsertBuilder builder) throws SQLException;

	int update(Connection conn, UpdateBuilder builder) throws SQLException;

	int delete(Connection conn, DeleteBuilder builder) throws SQLException;

	int create(Connection conn, CreateBuilder builder) throws SQLException;

	int drop(Connection conn, DropBuilder builder) throws SQLException;

}
