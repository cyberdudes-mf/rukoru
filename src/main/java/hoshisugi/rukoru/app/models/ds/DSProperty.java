package hoshisugi.rukoru.app.models.ds;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DSProperty {
	private BooleanProperty isEnable = new SimpleBooleanProperty(this, "isEnable");
	private StringProperty key = new SimpleStringProperty(this, "key");
	private StringProperty value = new SimpleStringProperty(this, "value");

	public DSProperty(final String property) {
		parseProperty(property);
	}

	public boolean getIsEnable() {
		return isEnable.get();
	}

	public void setIsEnable(final boolean isEnable) {
		this.isEnable.set(isEnable);
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

	public BooleanProperty getIsEnableProperty() {
		return isEnable;
	}

	public void setIsEnableProperty(final BooleanProperty isEnable) {
		this.isEnable = isEnable;
	}

	public StringProperty getKeyProperty() {
		return key;
	}

	public void setKeyProperty(final StringProperty key) {
		this.key = key;
	}

	public StringProperty getValueProperty() {
		return value;
	}

	public void setValueProperty(final StringProperty value) {
		this.value = value;
	}

	private void parseProperty(String property) {
		if (property.startsWith("#")) {
			property = property.substring(1);
			setIsEnable(false);
		} else {
			setIsEnable(true);
		}
		final String[] s = property.split("=");
		setKey(s[0]);
		if (s.length == 2) {
			setValue(s[1]);
		} else {
			setValue("");
		}
	}
}
