package hoshisugi.rukoru.app.models.ec2;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Tag {

	private final StringProperty key = new SimpleStringProperty(this, "key");

	private final StringProperty value = new SimpleStringProperty(this, "value");

	public Tag() {
	}

	public Tag(final String key, final String value) {
		setKey(key);
		setValue(value);
	}

	public String getKey() {
		return key.get();
	}

	public void setKey(final String key) {
		this.key.set(key);
	}

	public String getValue() {
		return value.get();
	}

	public void setValue(final String value) {
		this.value.set(value);
	}

	public StringProperty keyProperty() {
		return key;
	}

	public StringProperty valueProperty() {
		return value;
	}

}
