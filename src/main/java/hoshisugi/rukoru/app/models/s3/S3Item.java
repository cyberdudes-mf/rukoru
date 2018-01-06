package hoshisugi.rukoru.app.models.s3;

import java.util.Comparator;
import java.util.Date;

import com.google.common.base.Strings;

import hoshisugi.rukoru.framework.util.AssetUtil;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class S3Item {

	public static final String DELIMITER = "/";

	private final StringProperty bucketName = new SimpleStringProperty(this, "bucketName");
	private final StringProperty key = new SimpleStringProperty(this, "key");
	private final StringProperty name = new SimpleStringProperty(this, "name");
	private final ObjectProperty<Date> lastModified = new SimpleObjectProperty<>(this, "lastModified");
	private final ObjectProperty<Long> size = new SimpleObjectProperty<>(this, "size");
	private final StringProperty storageClass = new SimpleStringProperty(this, "storageClass");
	private final StringProperty owner = new SimpleStringProperty(this, "owner");
	private final ObjectProperty<S3Item> parent = new SimpleObjectProperty<>(this, "parent");
	private final ObservableList<S3Item> items = FXCollections.observableArrayList();

	private TreeItem<S3Item> treeItem;

	public enum Type {
		Root, Bucket, Folder, Object;
	}

	public S3Item() {
		items.addListener(this::onItemChanged);
	}

	public abstract Type getType();

	public String getBucketName() {
		return bucketName.get();
	}

	public void setBucketName(final String bucketName) {
		this.bucketName.set(bucketName);
	}

	public String getKey() {
		return key.get();
	}

	public void setKey(final String key) {
		this.key.set(key);
	}

	public String getName() {
		return name.get();
	}

	public void setName(final String name) {
		if (name.contains(DELIMITER)) {
			final String[] split = name.split(DELIMITER);
			this.name.set(split[split.length - 1]);
		} else {
			this.name.set(name);
		}
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

	public StringProperty backetNameProperty() {
		return bucketName;
	}

	public StringProperty keyProperty() {
		return key;
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
		return AssetUtil.getImage("16x16/amazon_s3.png");
	}

	public String getPath() {
		final String bucketName = getBucketName();
		if (Strings.isNullOrEmpty(bucketName)) {
			return null;
		}
		final String key = getKey();
		if (Strings.isNullOrEmpty(key)) {
			return bucketName;
		}

		return String.format("%s/%s", bucketName, key);
	}

	public void sort(final Comparator<S3Item> comparator) {
		items.sort(comparator);
		items.stream().forEach(i -> i.sort(comparator));
		if (isContainer()) {
			getTreeItem().getChildren().sort((o1, o2) -> comparator.compare(o1.getValue(), o2.getValue()));
		}
	}

	public boolean isContainer() {
		return true;
	}

	public TreeItem<S3Item> getTreeItem() {
		if (!isContainer()) {
			return null;
		}
		if (treeItem == null) {
			treeItem = new TreeItem<>(this, new ImageView(getIcon()));
		}
		return treeItem;
	}

	private void onItemChanged(final Change<? extends S3Item> change) {
		while (change.next()) {
			for (final S3Item removed : change.getRemoved()) {
				removed.setParent(null);
				if (removed.isContainer()) {
					getTreeItem().getChildren().remove(removed.getTreeItem());
				}
			}
			for (final S3Item added : change.getAddedSubList()) {
				added.setParent(this);
				if (added.isContainer()) {
					getTreeItem().getChildren().add(added.getTreeItem());
				}
			}
		}
	}
}
