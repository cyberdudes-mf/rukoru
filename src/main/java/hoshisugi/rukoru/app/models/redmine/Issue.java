package hoshisugi.rukoru.app.models.redmine;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Issue {

	private final IntegerProperty id = new SimpleIntegerProperty(this, "id");

	public Integer getId() {
		return id.get();
	}

	public void setId(final Integer id) {
		this.id.set(id);
	}

	public IntegerProperty idProperty() {
		return id;
	}
}
