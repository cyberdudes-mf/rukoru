package hoshisugi.rukoru.app.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Tag;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class EC2Instance implements Serializable {

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

	private final StringProperty instanceId = new SimpleStringProperty(this, "instanceId");
	private final StringProperty name = new SimpleStringProperty(this, "name");
	private final StringProperty instanceType = new SimpleStringProperty(this, "instanceType");
	private final StringProperty state = new SimpleStringProperty(this, "state");
	private final StringProperty publicIpAddress = new SimpleStringProperty(this, "publicIpAddress");
	private final StringProperty launchTime = new SimpleStringProperty(this, "launchTime");
	private final BooleanProperty autoStop = new SimpleBooleanProperty(this, "autoStop");

	public EC2Instance() {
	}

	public EC2Instance(final Instance instance) {
		final Map<String, String> tags = instance.getTags().stream()
				.collect(Collectors.toMap(Tag::getKey, Tag::getValue));
		setInstanceId(instance.getInstanceId());
		setName(tags.get("Name"));
		setAutoStop(Boolean.parseBoolean(tags.get("AutoStop")));
		setInstanceType(instance.getInstanceType());
		setPublicIpAddress(instance.getPublicIpAddress());
		final LocalDateTime dateTime = LocalDateTime.ofInstant(instance.getLaunchTime().toInstant(),
				ZoneId.systemDefault());
		setLaunchTime(formatter.format(dateTime));
		setState(instance.getState().getName());
	}

	public String getInstanceId() {
		return instanceId.get();
	}

	public void setInstanceId(final String instanceId) {
		this.instanceId.set(instanceId);
	}

	public String getName() {
		return name.get();
	}

	public void setName(final String name) {
		this.name.set(name);
	}

	public String getInstanceType() {
		return instanceType.get();
	}

	public void setInstanceType(final String instanceType) {
		this.instanceType.set(instanceType);
	}

	public String getState() {
		return state.get();
	}

	public void setState(final String state) {
		this.state.set(state);
	}

	public String getPublicIpAddress() {
		return publicIpAddress.get();
	}

	public void setPublicIpAddress(final String publicIpAddress) {
		this.publicIpAddress.set(publicIpAddress);
	}

	public String getLaunchTime() {
		return launchTime.get();
	}

	public void setLaunchTime(final String launchTime) {
		this.launchTime.set(launchTime);
	}

	public Boolean getAutoStop() {
		return autoStop.get();
	}

	public void setAutoStop(final Boolean autoStop) {
		this.autoStop.set(autoStop);
	}

	public StringProperty instanceIdProperty() {
		return instanceId;
	}

	public StringProperty nameProperty() {
		return name;
	}

	public StringProperty instanceTypeProperty() {
		return instanceType;
	}

	public StringProperty stateProperty() {
		return state;
	}

	public StringProperty publicIpAddressProperty() {
		return publicIpAddress;
	}

	public StringProperty launchTimeProperty() {
		return launchTime;
	}

	public BooleanProperty autoStopProperty() {
		return autoStop;
	}

}
