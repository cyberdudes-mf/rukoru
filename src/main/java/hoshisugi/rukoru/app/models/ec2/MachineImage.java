package hoshisugi.rukoru.app.models.ec2;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.amazonaws.services.ec2.model.Image;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MachineImage implements Serializable {

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

	private final StringProperty imageId = new SimpleStringProperty(this, "imageId");
	private final StringProperty name = new SimpleStringProperty(this, "name");
	private final StringProperty state = new SimpleStringProperty(this, "state");
	private final StringProperty creationDate = new SimpleStringProperty(this, "creationDate");

	public MachineImage() {
	}

	public MachineImage(final Image image) {
		update(image);
	}

	public void update(final Image image) {
		setImageId(image.getImageId());
		setName(image.getName());
		setState(image.getState());
		setCreationDate(toDefaultZonedDateTime(image.getCreationDate()).format(formatter));
	}

	public String getImageId() {
		return imageId.get();
	}

	public void setImageId(final String imageId) {
		this.imageId.set(imageId);
	}

	public String getName() {
		return name.get();
	}

	public void setName(final String name) {
		this.name.set(name);
	}

	public String getState() {
		return state.get();
	}

	public void setState(final String state) {
		this.state.set(state);
	}

	public String getCreationDate() {
		return creationDate.get();
	}

	public void setCreationDate(final String creationDate) {
		this.creationDate.set(creationDate);
	}

	public StringProperty imageIdProperty() {
		return imageId;
	}

	public StringProperty nameProperty() {
		return name;
	}

	public StringProperty stateProperty() {
		return state;
	}

	public StringProperty creationDateProperty() {
		return creationDate;
	}

	private ZonedDateTime toDefaultZonedDateTime(final String isoDateTime) {
		final ZonedDateTime utc = ZonedDateTime.parse(isoDateTime, ISO_DATE_TIME);
		return utc.withZoneSameInstant(ZoneId.systemDefault());
	}
}
