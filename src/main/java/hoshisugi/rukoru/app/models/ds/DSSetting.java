package hoshisugi.rukoru.app.models.ds;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;

import com.google.common.util.concurrent.UncheckedExecutionException;

import hoshisugi.rukoru.app.enums.DSSettingState;
import hoshisugi.rukoru.app.enums.ExecutionType;
import hoshisugi.rukoru.app.models.settings.DBEntity;
import hoshisugi.rukoru.framework.util.AssetUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DSSetting extends DBEntity {
	private final StringProperty name = new SimpleStringProperty(this, "name");
	private final StringProperty executionPath = new SimpleStringProperty(this, "executionPath");
	private final StringProperty executionType = new SimpleStringProperty(this, "executionType");
	private final StringProperty state = new SimpleStringProperty(this, "state");

	private static String dsServerName;
	private static String dsStudioName;

	static {
		setServerName();
		setStudioName();
	}

	public DSSetting() {
	}

	public DSSetting(final ResultSet rs) {
		try {
			setId(rs.getInt("id"));
			setName(rs.getString("name"));
			setExecutionPath(rs.getString("executionPath"));
			setExecutionType(ExecutionType.of(rs.getString("executiontype")));
			setCreatedAt(rs.getTimestamp("created_at"));
			setUpdatedAt(rs.getTimestamp("updated_at"));
			setState(DSSettingState.Update);
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

	public String getExecutionPath() {
		return executionPath.get();
	}

	public void setExecutionPath(final String dsHome) {
		this.executionPath.set(dsHome);
	}

	public String getExecutionType() {
		return executionType.get();
	}

	public void setExecutionType(final String executionType) {
		this.executionType.set(executionType);
	}

	public String getState() {
		return state.get();
	}

	public void setState(final DSSettingState state) {
		this.state.set(state.toString());
	}

	public StringProperty nameProperty() {
		return name;
	}

	public StringProperty executionPathProperty() {
		return executionPath;
	}

	public StringProperty executionTypeProperty() {
		return executionType;
	}

	public StringProperty stateProperty() {
		return state;
	}

	public Optional<String> getServiceName() throws IOException {
		final Properties properties = new Properties();
		properties.load(Files.newInputStream(Paths.get(getExecutionPath() + "/Uninstall/installvariables.properties")));
		return Optional.ofNullable(properties.getProperty("SHORT_SERVICE_NAME"));
	}

	private static void setServerName() {
		dsServerName = AssetUtil.loadProperties("ds.properties").getProperty("ds.server");
	}

	public static String getServerName() {
		return dsServerName;
	}

	private static void setStudioName() {
		dsStudioName = AssetUtil.loadProperties("ds.properties").getProperty("ds.studio");
	}

	public static String getStudioName() {
		return dsStudioName;
	}
}
