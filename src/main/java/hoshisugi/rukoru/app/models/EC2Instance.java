package hoshisugi.rukoru.app.models;

import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Tag;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class EC2Instance {

	private final StringProperty name = new SimpleStringProperty(this, "name");
	private final StringProperty instanceType = new SimpleStringProperty(this, "instanceType");
	private final StringProperty state = new SimpleStringProperty(this, "state");
	private final StringProperty publicIpAddress = new SimpleStringProperty(this, "publicIpAddress");
	private final ObjectProperty<Date> launchTime = new SimpleObjectProperty<>(this, "launchTime");
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
		setLaunchTime(instance.getLaunchTime());
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

	public Date getLaunchTime() {
		return launchTime.get();
	}

	public void setLaunchTime(final Date launchTime) {
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

	public ObjectProperty<Date> launchTimeProperty() {
		return launchTime;
	}

	public StringProperty autoStopProperty() {
		return autoStop;
	}

}
