package hoshisugi.rukoru.app.models.redmine;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CustomField {

	private final IntegerProperty id = new SimpleIntegerProperty(this, "id");
	private final StringProperty name = new SimpleStringProperty(this, "name");
	private final StringProperty value = new SimpleStringProperty(this, "value");

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

	public String getValue() {
		return value.get();
	}

	public void setValue(final String value) {
		this.value.set(value);
	}

	public IntegerProperty idProperty() {
		return id;
	}

	public StringProperty nameProperty() {
		return name;
	}

	public StringProperty valueProperty() {
		return value;
	}

}
