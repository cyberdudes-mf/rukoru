package hoshisugi.rukoru.app.models.ds;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

public class DSProperty {

	private final StringProperty statement = new SimpleStringProperty(this, "statement");
	private final BooleanProperty enable = new SimpleBooleanProperty(this, "enable");
	private final StringProperty key = new SimpleStringProperty(this, "key");
	private final StringProperty value = new SimpleStringProperty(this, "value");
	private final DSPropertyManager manager;

	public DSProperty(final String enable, final String key, final String value, final DSPropertyManager manager) {
		setEnable(enable == null);
		setKey(key);
		setValue(value);
		this.manager = manager;
		updateStatement();
		this.enable.addListener(this::onPropertyChenged);
		this.key.addListener(this::onKeyPropertyChenged);
		this.value.addListener(this::onPropertyChenged);
		statement.addListener(this::onStatementPropertyChanged);
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

	private void updateStatement() {
		statement.set((isEnable() ? "" : "#") + getKey() + "=" + getValue());
	}

	private void onKeyPropertyChenged(final ObservableValue<? extends String> observable, final String oldValue,
			final String newValue) {
		if (newValue.isEmpty()) {
			setKey(oldValue);
			return;
		}
		updateStatement();
	}

	private <S> void onPropertyChenged(final ObservableValue<? extends S> observable, final S oldValue,
			final S newValue) {
		updateStatement();
	}

	private void onStatementPropertyChanged(final ObservableValue<? extends String> observable, final String oldValue,
			final String newValue) {
		manager.replace(oldValue, newValue);
	}

}
