package hoshisugi.rukoru.app.models.settings;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.common.util.concurrent.UncheckedExecutionException;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DSSetting extends DBEntity {
	private final StringProperty name = new SimpleStringProperty(this, "name");
	private final StringProperty dsHome = new SimpleStringProperty(this, "dsHome");
	private final IntegerProperty executionType = new SimpleIntegerProperty(this, "executionType");

	public DSSetting(final ResultSet rs) {
		try {
			setId(rs.getInt("id"));
			setName(rs.getString("name"));
			setDSHome(rs.getString("ds"));
			setExecutionType(rs.getInt("execution"));
			setCreatedAt(rs.getTimestamp("created_at"));
			setUpdatedAt(rs.getTimestamp("updated_at"));
		} catch (final SQLException e) {
			throw new UncheckedExecutionException(e);
		}
	}

	public String getName() {
		return name.get();
	}

	public void setName(final String name) {
		this.name.set(name);
	}

	public String getDataSpiderHome() {
		return dsHome.get();
	}

	public void setDSHome(final String path) {
		this.dsHome.set(path);
	}

	public int getExecutionType() {
		return executionType.get();
	}

	public void setExecutionType(final int execution) {
		this.executionType.set(execution);
	}
}
