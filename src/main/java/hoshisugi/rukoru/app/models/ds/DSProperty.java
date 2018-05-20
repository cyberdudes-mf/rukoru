package hoshisugi.rukoru.app.models.ds;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class DSProperty {

	private final StringProperty statement = new SimpleStringProperty();
	private final BooleanProperty isEnable = new SimpleBooleanProperty(this, "isEnable");
	private final StringProperty key = new SimpleStringProperty(this, "key");
	private final StringProperty value = new SimpleStringProperty(this, "value");

	public DSProperty(final String property, final ChangeListener<String> listener) {
		parseProperty(property);
		statement.set((isEnable.get() ? "" : "#") + key.get() + "=" + value.get());
		isEnable.addListener(this::onPropertyChenged);
		key.addListener(this::onPropertyChenged);
		value.addListener(this::onPropertyChenged);
		statement.addListener(listener);
	}

	public Boolean getIsEnable() {
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

	public StringProperty statementProperty() {
		return statement;
	}

	public BooleanProperty isEnableProperty() {
		return isEnable;
	}

	public StringProperty keyProperty() {
		return key;
	}

	public StringProperty valueProperty() {
		return value;
	}

	@Override
	public String toString() {
		return statement.get();
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

	private <S> void onPropertyChenged(final ObservableValue<? extends S> observable, final S oldValue,
			final S newValue) {
		statement.set((isEnable.get() ? "" : "#") + key.get() + "=" + value.get());
	}

}
