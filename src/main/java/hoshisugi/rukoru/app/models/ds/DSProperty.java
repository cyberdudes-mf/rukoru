package hoshisugi.rukoru.app.models.ds;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

public class DSProperty {

	private final StringProperty article = new SimpleStringProperty(this, "article");
	private final BooleanProperty enable = new SimpleBooleanProperty(this, "enable");
	private final StringProperty key = new SimpleStringProperty(this, "key");
	private final StringProperty value = new SimpleStringProperty(this, "value");
	private final DSPropertyManager manager;

	public DSProperty(final String enable, final String key, final String value, final DSPropertyManager manager) {
		setEnable(enable == null);
		setKey(key);
		setValue(value);
		this.manager = manager;
		updateArticle();
		this.enable.addListener(this::onPropertyChenged);
		this.key.addListener(this::onKeyPropertyChenged);
		this.value.addListener(this::onPropertyChenged);
		article.addListener(this::onArticlePropertyChanged);
	}

	public String getArticle() {
		return article.get();
	}

	public void setArticle(final String article) {
		this.article.set(article);
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

	public StringProperty articleProperty() {
		return article;
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

	private void updateArticle() {
		article.set((isEnable() ? "" : "#") + getKey() + "=" + getValue());
	}

	private void onKeyPropertyChenged(final ObservableValue<? extends String> observable, final String oldValue,
			final String newValue) {
		if (newValue.isEmpty()) {
			setKey(oldValue);
			return;
		}
		updateArticle();
	}

	private <S> void onPropertyChenged(final ObservableValue<? extends S> observable, final S oldValue,
			final S newValue) {
		updateArticle();
	}

	private void onArticlePropertyChanged(final ObservableValue<? extends String> observable, final String oldValue,
			final String newValue) {
		manager.replace(oldValue, newValue);
	}

}
