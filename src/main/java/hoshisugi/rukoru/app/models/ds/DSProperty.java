package hoshisugi.rukoru.app.models.ds;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class DSProperty {

	private final StringProperty statement = new SimpleStringProperty(this, "statement");
	private final BooleanProperty enable = new SimpleBooleanProperty(this, "enable");
	private final StringProperty key = new SimpleStringProperty(this, "key");
	private final StringProperty value = new SimpleStringProperty(this, "value");

	public DSProperty(final String property, final ChangeListener<String> listener) {
		parseProperty(property);
		updateStatement();
		enable.addListener(this::onPropertyChenged);
		key.addListener(this::onPropertyChenged);
		value.addListener(this::onPropertyChenged);
		statement.addListener(listener);
	}

	public String getStatement() {
		return statement.get();
	}

	public void setStatement(final String statement) {
		this.statement.set(statement);
	}

	public Boolean isEnable() {
		return enable.get();
	}

	public void setEnable(final boolean enable) {
		this.enable.set(enable);
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

	public BooleanProperty enableProperty() {
		return enable;
	}

	public StringProperty keyProperty() {
		return key;
	}

	public StringProperty valueProperty() {
		return value;
	}

	private void parseProperty(String property) {
		if (property.startsWith("#")) {
			property = property.substring(1);
			setEnable(false);
		} else {
			setEnable(true);
		}
		final String[] s = property.split("=");
		setKey(s[0]);
		if (s.length == 2) {
			setValue(s[1]);
		} else {
			setValue("");
		}
	}

	private void updateStatement() {
		statement.set((isEnable() ? "" : "#") + getKey() + "=" + getValue());
	}

	private <S> void onPropertyChenged(final ObservableValue<? extends S> observable, final S oldValue,
			final S newValue) {
		updateStatement();
	}

}
