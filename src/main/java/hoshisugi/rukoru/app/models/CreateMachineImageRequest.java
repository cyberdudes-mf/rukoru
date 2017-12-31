package hoshisugi.rukoru.app.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CreateMachineImageRequest {

	private final StringProperty instanceId = new SimpleStringProperty(this, "instanceId");
	private final StringProperty name = new SimpleStringProperty(this, "name");
	private final StringProperty description = new SimpleStringProperty(this, "description");
	private final BooleanProperty noReboot = new SimpleBooleanProperty(this, "noReboot");
	private final ObservableList<Tag> tags = FXCollections.observableArrayList();

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

	public String getDescription() {
		return description.get();
	}

	public void setDescription(final String description) {
		this.description.set(description);
	}

	public boolean isNoReboot() {
		return noReboot.get();
	}

	public void setNoReboot(final boolean noReboot) {
		this.noReboot.set(noReboot);
	}

	public ObservableList<Tag> getTags() {
		return tags;
	}

	public StringProperty instanceIdProperty() {
		return instanceId;
	}

	public StringProperty nameProperty() {
		return name;
	}

	public StringProperty descriptionProperty() {
		return description;
	}

	public BooleanProperty noRebootProperty() {
		return noReboot;
	}

}
