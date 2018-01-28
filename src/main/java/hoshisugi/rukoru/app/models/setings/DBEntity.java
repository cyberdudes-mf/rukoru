package hoshisugi.rukoru.app.models.setings;

import java.io.Serializable;
import java.sql.Timestamp;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class DBEntity implements Serializable {

	private final ObjectProperty<Integer> id = new SimpleObjectProperty<>(this, "id");

	private final ObjectProperty<Timestamp> createdAt = new SimpleObjectProperty<>(this, "createdAt");

	private final ObjectProperty<Timestamp> updatedAt = new SimpleObjectProperty<>(this, "updatedAt");

	public Integer getId() {
		return id.get();
	}

	public void setId(final Integer id) {
		this.id.set(id);
	}

	public Timestamp getCreatedAt() {
		return createdAt.get();
	}

	public void setCreatedAt(final Timestamp createdAt) {
		this.createdAt.set(createdAt);
	}

	public Timestamp getUpdatedAt() {
		return updatedAt.get();
	}

	public void setUpdatedAt(final Timestamp updatedAt) {
		this.updatedAt.set(updatedAt);
	}

	public ObjectProperty<Integer> idProperty() {
		return id;
	}

	public ObjectProperty<Timestamp> createdAtProperty() {
		return createdAt;
	}

	public ObjectProperty<Timestamp> updatedAtProperty() {
		return updatedAt;
	}

}
