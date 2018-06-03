package hoshisugi.rukoru.app.models.ds;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public interface DSPropertiesContent {
	Pattern PATTERN = Pattern.compile("(?<enable>#)?(?<key>\\S+)=(?<value>\\S*)");

	boolean isProperty();

	String getArticle();

	static DSPropertiesContent newContent(final String line) {
		final Matcher matcher = PATTERN.matcher(line);
		if (matcher.matches()) {
			final boolean enable = matcher.group("enable") == null;
			final String key = matcher.group("key");
			final String value = matcher.group("value");
			return new Property(enable, key, value);
		} else {
			return new Comment(line);
		}
	}

	default void write(final BufferedWriter writer) throws IOException {
		writer.write(getArticle());
	}

	static class Property implements DSPropertiesContent {
		private final BooleanProperty enable = new SimpleBooleanProperty(this, "enable");
		private final StringProperty key = new SimpleStringProperty(this, "key");
		private final StringProperty value = new SimpleStringProperty(this, "value");

		public Property(final boolean enable, final String key, final String value) {
			setEnable(enable);
			setKey(key);
			setValue(value);
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

		public BooleanProperty enableProperty() {
			return enable;
		}

		public StringProperty keyProperty() {
			return key;
		}

		public StringProperty valueProperty() {
			return value;
		}

		@Override
		public boolean isProperty() {
			return true;
		}

		@Override
		public String getArticle() {
			return (isEnable() ? "" : "#") + getKey() + "=" + getValue();
		}

	}

	static class Comment implements DSPropertiesContent {

		private final String comment;

		public Comment(final String comment) {
			super();
			this.comment = comment;
		}

		@Override
		public boolean isProperty() {
			return false;
		}

		@Override
		public String getArticle() {
			return comment;
		}

	}
}
