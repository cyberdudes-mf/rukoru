package hoshisugi.rukoru.app.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Tag;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class EC2Instance implements Serializable {

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

	private final StringProperty name = new SimpleStringProperty(this, "name");
	private final StringProperty instanceType = new SimpleStringProperty(this, "instanceType");
	private final StringProperty state = new SimpleStringProperty(this, "state");
	private final StringProperty publicIpAddress = new SimpleStringProperty(this, "publicIpAddress");
	private final StringProperty launchTime = new SimpleStringProperty(this, "launchTime");
	private final StringProperty autoStop = new SimpleStringProperty(this, "autoStop");

	public EC2Instance() {
	}

	public EC2Instance(final Instance instance) {
		final Map<String, String> tags = instance.getTags().stream()
				.collect(Collectors.toMap(Tag::getKey, Tag::getValue));
		setName(tags.get("Name"));
		setAutoStop(tags.get("AutoStop"));
		setInstanceType(instance.getInstanceType());
		setIpAddress(instance.getPublicIpAddress());
		final LocalDateTime dateTime = LocalDateTime.ofInstant(instance.getLaunchTime().toInstant(),
				ZoneId.systemDefault());
		setLaunchTime(formatter.format(dateTime));
		setState(instance.getState().getName());
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

	public String getIpAddress() {
		return publicIpAddress.get();
	}

	public void setIpAddress(final String publicIpAddress) {
		this.publicIpAddress.set(publicIpAddress);
	}

	public String getLaunchTime() {
		return launchTime.get();
	}

	public void setLaunchTime(final String launchTime) {
		this.launchTime.set(launchTime);
	}

	public String getAutoStop() {
		return autoStop.get();
	}

	public void setAutoStop(final String autoStop) {
		this.autoStop.set(autoStop);
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

	public StringProperty autoStopProperty() {
		return autoStop;
	}

}
