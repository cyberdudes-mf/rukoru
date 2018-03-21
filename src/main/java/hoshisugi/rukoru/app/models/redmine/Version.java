package hoshisugi.rukoru.app.models.redmine;

import java.util.Date;
import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Version {

	public ObjectProperty<Date> created_on = new SimpleObjectProperty<>(this, "created_on");
	public ObservableList<CustomField> custom_fields = FXCollections.observableArrayList();
	public StringProperty description = new SimpleStringProperty(this, "description");
	public ObjectProperty<Date> due_date = new SimpleObjectProperty<>(this, "due_date");
	public IntegerProperty id = new SimpleIntegerProperty(this, "id");
	public StringProperty name = new SimpleStringProperty(this, "name");
	public ObjectProperty<Project> project = new SimpleObjectProperty<>(this, "project");
	public StringProperty sharing = new SimpleStringProperty(this, "sharing");
	public StringProperty status = new SimpleStringProperty(this, "status");
	public ObjectProperty<Date> updated_on = new SimpleObjectProperty<>(this, "updated_on");

	public Date getCreated_on() {
		return created_on.get();
	}

	public void setCreated_on(final Date created_on) {
		this.created_on.set(created_on);
	}

	public ObservableList<CustomField> getCustom_fields() {
		return custom_fields;
	}

	public void setCustom_fields(final List<CustomField> custom_fields) {
		this.custom_fields.setAll(custom_fields);
	}

	public String getDescription() {
		return description.get();
	}

	public void setDescription(final String description) {
		this.description.set(description);
	}

	public Date getDue_date() {
		return due_date.get();
	}

	public void setDue_date(final Date due_date) {
		this.due_date.set(due_date);
	}

	public Integer getId() {
		return id.get();
	}

	public void setId(final Integer id) {
		this.id.set(id);
	}

	public String getName() {
		return name.get();
	}

	public void setName(final String name) {
		this.name.set(name);
	}

	public Project getProject() {
		return project.get();
	}

	public void setProject(final Project project) {
		this.project.set(project);
	}

	public String getSharing() {
		return sharing.get();
	}

	public void setSharing(final String sharing) {
		this.sharing.set(sharing);
	}

	public String getStatus() {
		return status.get();
	}

	public void setStatus(final String status) {
		this.status.set(status);
	}

	public Date getUpdated_on() {
		return updated_on.get();
	}

	public void setUpdated_on(final Date updated_on) {
		this.updated_on.set(updated_on);
	}

	public boolean isClosed() {
		return getStatus().equals("closed");
	}

	public ObjectProperty<Date> created_onProperty() {
		return created_on;
	}

	public StringProperty descriptionProperty() {
		return description;
	}

	public ObjectProperty<Date> due_dateProperty() {
		return due_date;
	}

	public IntegerProperty idProperty() {
		return id;
	}

	public StringProperty nameProperty() {
		return name;
	}

	public ObjectProperty<Project> projectProperty() {
		return project;
	}

	public StringProperty sharingProperty() {
		return sharing;
	}

	public StringProperty statusProperty() {
		return status;
	}

	public ObjectProperty<Date> updated_onProperty() {
		return updated_on;
	}

	public String getUrl() {
		return String.format("http://redmine.dataspidercloud.tokyo/rb/taskboards/%s", id.get());
	}
}
