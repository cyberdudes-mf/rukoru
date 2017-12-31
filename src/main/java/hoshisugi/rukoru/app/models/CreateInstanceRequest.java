package hoshisugi.rukoru.app.models;

import hoshisugi.rukoru.app.enums.InstanceType;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CreateInstanceRequest {

	private final StringProperty imageId = new SimpleStringProperty(this, "imageId");
	private final ObjectProperty<InstanceType> instanceType = new SimpleObjectProperty<>(this, "instanceType");
	private final IntegerProperty minCount = new SimpleIntegerProperty(this, "minCount");
	private final IntegerProperty maxCount = new SimpleIntegerProperty(this, "maxCount");
	private final StringProperty keyName = new SimpleStringProperty(this, "keyName");
	private final StringProperty securityGroup = new SimpleStringProperty(this, "securityGroup");
	private final ObservableList<Tag> tags = FXCollections.observableArrayList();

	public CreateInstanceRequest() {
	}

	public String getImageId() {
		return imageId.get();
	}

	public void setImageId(final String imageId) {
		this.imageId.set(imageId);
	}

	public InstanceType getInstanceType() {
		return instanceType.get();
	}

	public void setInstanceType(final InstanceType instanceType) {
		this.instanceType.set(instanceType);
	}

	public int getMinCount() {
		return minCount.get();
	}

	public void setMinCount(final int minCount) {
		this.minCount.set(minCount);
	}

	public int getMaxCount() {
		return maxCount.get();
	}

	public void setMaxCount(final int maxCount) {
		this.maxCount.set(maxCount);
	}

	public String getKeyName() {
		return keyName.get();
	}

	public void setKeyName(final String keyName) {
		this.keyName.set(keyName);
	}

	public String getSecurityGroup() {
		return securityGroup.get();
	}

	public void setSecurityGroup(final String securityGroup) {
		this.securityGroup.set(securityGroup);
	}

	public ObservableList<Tag> getTags() {
		return tags;
	}

	public StringProperty imageIdProperty() {
		return imageId;
	}

	public ObjectProperty<InstanceType> instanceTypeProperty() {
		return instanceType;
	}

	public IntegerProperty minCountProperty() {
		return minCount;
	}

	public IntegerProperty maxCountProperty() {
		return maxCount;
	}

	public StringProperty keyNameProperty() {
		return keyName;
	}

	public StringProperty securityGroupProperty() {
		return securityGroup;
	}

}
