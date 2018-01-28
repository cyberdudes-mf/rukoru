package hoshisugi.rukoru.app.models.settings;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import com.google.common.util.concurrent.UncheckedExecutionException;

import hoshisugi.rukoru.app.services.settings.LocalSettingService;
import hoshisugi.rukoru.framework.inject.Injector;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RepositoryDBConnection extends DBEntity {

	private final StringProperty instanceName = new SimpleStringProperty(this, "instanceName");

	private final StringProperty endpoint = new SimpleStringProperty(this, "endpoint");

	private final IntegerProperty port = new SimpleIntegerProperty(this, "port");

	private final StringProperty username = new SimpleStringProperty(this, "username");

	private final StringProperty password = new SimpleStringProperty(this, "password");

	private static EntityCache<RepositoryDBConnection> cache = new EntityCache<>(RepositoryDBConnection::load);

	public RepositoryDBConnection() {
	}

	public RepositoryDBConnection(final ResultSet rs) {
		try {
			setId(rs.getInt("id"));
			setInstanceName(rs.getString("instance_name"));
			setEndpoint(rs.getString("endpoint"));
			setPort(rs.getInt("port"));
			setUsername(rs.getString("username"));
			setPassword(rs.getString("password"));
			setCreatedAt(rs.getTimestamp("created_at"));
			setUpdatedAt(rs.getTimestamp("updated_at"));
		} catch (final SQLException e) {
			throw new UncheckedExecutionException(e);
		}
	}

	public static RepositoryDBConnection get() {
		return cache.get();
	}

	public static boolean hasConnection() {
		return cache.hasEntity();
	}

	public static void reload() throws Exception {
		cache.reload();
	}

	private static Optional<RepositoryDBConnection> load() throws Exception {
		final LocalSettingService service = Injector.getInstance(LocalSettingService.class);
		return service.loadRepositoryDBConnection();
	}

	public String getInstanceName() {
		return instanceName.get();
	}

	public void setInstanceName(final String instanceName) {
		this.instanceName.set(instanceName);
	}

	public String getEndpoint() {
		return endpoint.get();
	}

	public void setEndpoint(final String endpoint) {
		this.endpoint.set(endpoint);
	}

	public Integer getPort() {
		return port.get();
	}

	public void setPort(final Integer port) {
		this.port.set(port);
	}

	public String getUsername() {
		return username.get();
	}

	public void setUsername(final String username) {
		this.username.set(username);
	}

	public String getPassword() {
		return password.get();
	}

	public void setPassword(final String username) {
		this.password.set(username);
	}

	public StringProperty instanceNameProperty() {
		return instanceName;
	}

	public StringProperty endpointProperty() {
		return endpoint;
	}

	public IntegerProperty portProperty() {
		return port;
	}

	public StringProperty usernameProperty() {
		return username;
	}

	public StringProperty passwordProperty() {
		return password;
	}

	public String getJdbcUrl() {
		return String.format("jdbc:mariadb://%s:%s", getEndpoint(), getPort());
	}
}
