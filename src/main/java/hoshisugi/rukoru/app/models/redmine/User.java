package hoshisugi.rukoru.app.models.redmine;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {

	private final IntegerProperty id = new SimpleIntegerProperty(this, "id");
	private final StringProperty name = new SimpleStringProperty(this, "name");

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

	public final IntegerProperty idProperty() {
		return id;
	}

	public final StringProperty nameProperty() {
		return name;
	}

}
