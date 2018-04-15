package hoshisugi.rukoru.app.models.settings;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.common.util.concurrent.UncheckedExecutionException;

import hoshisugi.rukoru.app.enums.Preferences;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Preference extends DBEntity {

	private final StringProperty category = new SimpleStringProperty(this, "category");

	private final StringProperty key = new SimpleStringProperty(this, "key");

	private final StringProperty value = new SimpleStringProperty(this, "value");

	public Preference(final String category, final String key, final String value) {
		setCategory(category);
		setKey(key);
		setValue(value);
	}

	public Preference(final Preferences preferences) {
		setCategory(preferences.category());
		setKey(preferences.key());
	}

	public Preference(final ResultSet rs) {
		try {
			setId(rs.getInt("id"));
			setCategory(rs.getString("category"));
			setKey(rs.getString("key"));
			setValue(rs.getString("value"));
			setCreatedAt(rs.getTimestamp("created_at"));
			setUpdatedAt(rs.getTimestamp("updated_at"));
		} catch (final SQLException e) {
			throw new UncheckedExecutionException(e);
		}
	}

	public String getCategory() {
		return category.get();
	}

	public void setCategory(final String category) {
		this.category.set(category);
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

	public StringProperty categoryProperty() {
		return category;
	}

	public StringProperty keyProperty() {
		return key;
	}

	public StringProperty valueProperty() {
		return value;
	}

}
