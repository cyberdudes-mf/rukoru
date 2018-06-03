package hoshisugi.rukoru.app.models.ds;

import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import hoshisugi.rukoru.app.models.ds.DSPropertiesContent.Property;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DSProperties implements DSPropertiesTreeNode {

	private final StringProperty fileName = new SimpleStringProperty(this, "fileName");

	private final ObjectProperty<Path> path = new SimpleObjectProperty<>(this, "path");

	private final ObservableList<DSPropertiesContent> contents = FXCollections.observableArrayList();

	public DSProperties(final Path path) {
		fileName.set(path.getFileName().toString());
		this.path.set(path);
	}

	public String getFileName() {
		return fileName.get();
	}

	public Path getPath() {
		return path.get();
	}

	public ObservableList<DSPropertiesContent> getContents() {
		return contents;
	}

	public StringProperty fileNameProperty() {
		return fileName;
	}

	public ObjectProperty<Path> pathProperty() {
		return path;
	}

	public List<Property> loadProperties() throws IOException {
		if (contents.isEmpty()) {
			Files.readAllLines(path.get()).stream().map(DSPropertiesContent::newContent).forEach(contents::add);
		}
		return contents.stream().filter(DSPropertiesContent::isProperty).map(Property.class::cast)
				.collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return getFileName();
	}

	public Property add(final String key, final String value) {
		final Property property = new Property(true, key, value);
		contents.add(property);
		return property;
	}

	public void remove(final String key) {
		final Optional<Property> property = contents.stream().filter(DSPropertiesContent::isProperty)
				.map(Property.class::cast).filter(p -> p.getKey().equals(key)).findFirst();
		property.ifPresent(contents::remove);
	}

	public void save() throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path.get(), WRITE, TRUNCATE_EXISTING)) {
			for (final DSPropertiesContent content : contents) {
				content.write(writer);
				writer.newLine();
			}
		}
	}
}
