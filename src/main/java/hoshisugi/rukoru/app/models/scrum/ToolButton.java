package hoshisugi.rukoru.app.models.scrum;

import java.net.URL;
import java.sql.ResultSet;

import com.google.common.util.concurrent.UncheckedExecutionException;

import hoshisugi.rukoru.app.models.settings.DBEntity;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public class ToolButton extends DBEntity {

	public enum Operation {
		Add, Edit, Delete,
	}

	private final StringProperty label = new SimpleStringProperty(this, "label");
	private final ObjectProperty<Color> color = new SimpleObjectProperty<>(this, "color");
	private final ObjectProperty<URL> url = new SimpleObjectProperty<>(this, "url");
	private final ObjectProperty<Operation> operation = new SimpleObjectProperty<>(this, "operation");
	private final IntegerProperty sortOrder = new SimpleIntegerProperty(this, "sortOrder", Integer.MAX_VALUE);

	public ToolButton() {
		setColor(Color.color(Math.random(), Math.random(), Math.random()));
	}

	public ToolButton(final ResultSet rs) {
		try {
			setId(rs.getInt("id"));
			setLabel(rs.getString("label"));
			setColor(Color.web(rs.getString("color")));
			setUrl(new URL(rs.getString("url")));
			setSortOrder(rs.getInt("sort_order"));
			setCreatedAt(rs.getTimestamp("created_at"));
			setUpdatedAt(rs.getTimestamp("updated_at"));
		} catch (final Exception e) {
			throw new UncheckedExecutionException(e);
		}
	}

	public String getLabel() {
		return label.get();
	}

	public void setLabel(final String label) {
		this.label.set(label);
	}

	public Color getColor() {
		return color.get();
	}

	public void setColor(final Color color) {
		this.color.set(color);
	}

	public URL getUrl() {
		return url.get();
	}

	public void setUrl(final URL url) {
		this.url.set(url);
	}

	public Operation getOperation() {
		return operation.get();
	}

	public void setOperation(final Operation operation) {
		this.operation.set(operation);
	}

	public Integer getSortOrder() {
		return sortOrder.get();
	}

	public void setSortOrder(final Integer sortOrder) {
		this.sortOrder.set(sortOrder);
	}

	public StringProperty labelProperty() {
		return label;
	}

	public ObjectProperty<Color> colorProperty() {
		return color;
	}

	public ObjectProperty<URL> urlProperty() {
		return url;
	}

	public ObjectProperty<Operation> operationProperty() {
		return operation;
	}

	public IntegerProperty sortOrderProperty() {
		return sortOrder;
	}

}
