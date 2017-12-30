package hoshisugi.rukoru.app.models;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.amazonaws.services.ec2.model.Image;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AMI implements Serializable {

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

	private final StringProperty name = new SimpleStringProperty(this, "name");
	private final StringProperty creationDate = new SimpleStringProperty(this, "creationDate");

	public AMI() {
	}

	public AMI(final Image image) {
		setName(image.getName());
		setCreationDate(formatter.format(LocalDateTime.parse(image.getCreationDate(), ISO_DATE_TIME)));
	}

	public String getName() {
		return name.get();
	}

	public void setName(final String name) {
		this.name.set(name);
	}

	public String getCreationDate() {
		return creationDate.get();
	}

	public void setCreationDate(final String creationDate) {
		this.creationDate.set(creationDate);
	}

	public StringProperty nameProperty() {
		return name;
	}

	public StringProperty creationDateProperty() {
		return creationDate;
	}

}
