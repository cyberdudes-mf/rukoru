package hoshisugi.rukoru.app.models.rds;

import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.Endpoint;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RDSInstance {

	private final StringProperty instanceName = new SimpleStringProperty(this, "instanceName");

	private final StringProperty endpoint = new SimpleStringProperty(this, "endpoint");

	private final IntegerProperty port = new SimpleIntegerProperty(this, "port");

	private final StringProperty username = new SimpleStringProperty(this, "username");

	public RDSInstance(final DBInstance instance) {
		setInstanceName(instance.getDBInstanceIdentifier());
		final Endpoint e = instance.getEndpoint();
		setEndpoint(e.getAddress());
		setPort(e.getPort());
		setUsername(instance.getMasterUsername());
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

}
