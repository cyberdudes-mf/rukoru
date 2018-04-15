package hoshisugi.rukoru.app.models.redmine;

import java.util.Date;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Project {

	private final IntegerProperty id = new SimpleIntegerProperty(this, "id");
	private final StringProperty name = new SimpleStringProperty(this, "name");
	private final StringProperty description = new SimpleStringProperty(this, "description");
	private final StringProperty identifier = new SimpleStringProperty(this, "identifier");
	private final BooleanProperty is_public = new SimpleBooleanProperty(this, "is_public");
	private final IntegerProperty status = new SimpleIntegerProperty(this, "status");
	private final ObservableList<CustomField> custom_fields = FXCollections.observableArrayList();
	private final ObjectProperty<Date> created_on = new SimpleObjectProperty<>(this, "created_on");
	private final ObjectProperty<Date> updated_on = new SimpleObjectProperty<>(this, "created_on");

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

	public Integer getId() {
		return id.get();
	}

	public void setId(final Integer id) {
		this.id.set(id);
	}

	public String getIdentifier() {
		return identifier.get();
	}

	public void setIdentifier(final String identifier) {
		this.identifier.set(identifier);
	}

	public Boolean getIs_public() {
		return is_public.get();
	}

	public void setIs_public(final Boolean is_public) {
		this.is_public.set(is_public);
	}

	public String getName() {
		return name.get();
	}

	public void setName(final String name) {
		this.name.set(name);
	}

	public Integer getStatus() {
		return status.get();
	}

	public void setStatus(final Integer status) {
		this.status.set(status);
	}

	public Date getUpdated_on() {
		return updated_on.get();
	}

	public void setUpdated_on(final Date updated_on) {
		this.updated_on.set(updated_on);
	}

	public IntegerProperty idProperty() {
		return id;
	}

	public StringProperty nameProperty() {
		return name;
	}

	public StringProperty descriptionProperty() {
		return description;
	}

	public StringProperty identifierProperty() {
		return identifier;
	}

	public BooleanProperty is_publicProperty() {
		return is_public;
	}

	public IntegerProperty statusProperty() {
		return status;
	}

	public ObjectProperty<Date> created_onProperty() {
		return created_on;
	}

	public ObjectProperty<Date> updated_onProperty() {
		return updated_on;
	}
}
