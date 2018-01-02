package hoshisugi.rukoru.app.models;

import java.util.Date;

import hoshisugi.rukoru.flamework.util.AssetUtil;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class S3Item {

	private final StringProperty name = new SimpleStringProperty(this, "name");
	private final ObjectProperty<Date> lastModified = new SimpleObjectProperty<>(this, "lastModified");
	private final ObjectProperty<Long> size = new SimpleObjectProperty<>(this, "size");
	private final StringProperty storageClass = new SimpleStringProperty(this, "storageClass");
	private final StringProperty owner = new SimpleStringProperty(this, "owner");
	private final ObjectProperty<S3Item> parent = new SimpleObjectProperty<>(this, "parent");
	private final ObservableList<S3Item> items = FXCollections.observableArrayList();

	public S3Item() {
	}

	public S3Item(final String name) {
		setName(name);
	}

	public String getName() {
		return name.get();
	}

	public void setName(final String name) {
		this.name.set(name);
	}

	public Date getLastModified() {
		return lastModified.get();
	}

	public void setLastModified(final Date lastModified) {
		this.lastModified.set(lastModified);
	}

	public Long getSize() {
		return size.get();
	}

	public void setSize(final Long size) {
		this.size.set(size);
	}

	public String getStorageClass() {
		return storageClass.get();
	}

	public void setStorageClass(final String storageClass) {
		this.storageClass.set(storageClass);
	}

	public String getOwner() {
		return owner.get();
	}

	public void setOwner(final String owner) {
		this.owner.set(owner);
	}

	public S3Item getParent() {
		return parent.get();
	}

	public void setParent(final S3Item parent) {
		this.parent.set(parent);
	}

	public StringProperty nameProperty() {
		return name;
	}

	public ObjectProperty<Date> lastModifiedProperty() {
		return lastModified;
	}

	public ObjectProperty<Long> sizeProperty() {
		return size;
	}

	public StringProperty storageClassProperty() {
		return storageClass;
	}

	public StringProperty ownerProperty() {
		return owner;
	}

	public ObjectProperty<S3Item> parentProperty() {
		return parent;
	}

	public ObservableList<S3Item> getItems() {
		return items;
	}

	@Override
	public String toString() {
		return getName();
	}

	public Image getIcon() {
		return AssetUtil.getImage("s3_16x16.png");
	}

	public TreeItem<S3Item> toTreeItem() {
		return new TreeItem<>(this, new ImageView(getIcon()));
	}

}
