package hoshisugi.rukoru.app.models.repositorydb;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.common.util.concurrent.UncheckedExecutionException;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RepositoryDB {
	private final StringProperty name = new SimpleStringProperty(this, "name");

	public RepositoryDB() {

	}

	public RepositoryDB(final ResultSet rs) {
		try {
			update(rs.getString(1));
		} catch (final SQLException e) {
			throw new UncheckedExecutionException(e);
		}
	}

	public void update(final String name) {
		setName(name);
	}

	public String getName() {
		return name.get();
	}

	public void setName(final String name) {
		this.name.set(name);
	}

	public StringProperty nameProperty() {
		return name;
	}
}
