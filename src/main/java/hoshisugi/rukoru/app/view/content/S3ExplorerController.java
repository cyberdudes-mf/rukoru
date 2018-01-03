package hoshisugi.rukoru.app.view.content;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.AuthSetting;
import hoshisugi.rukoru.app.models.S3Item;
import hoshisugi.rukoru.app.services.s3.S3Service;
import hoshisugi.rukoru.flamework.controls.BaseController;
import hoshisugi.rukoru.flamework.util.ConcurrentUtil;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;

public class S3ExplorerController extends BaseController {

	@FXML
	private SplitPane splitPane;

	@Inject
	private S3Service s3Service;

	private final ObjectProperty<S3Item> selectedItem = new SimpleObjectProperty<>(this, "selectedItem",
			new S3Item("Amazon S3"));

	private S3Item rootItem;

	@Override
	public void initialize(final URL url, final ResourceBundle resource) {
		rootItem = getSelectedItem();
		selectedItem.addListener(this::selectedItemChanged);
	}

	public S3Item getSelectedItem() {
		return selectedItem.get();
	}

	public void setSelectedItem(final S3Item item) {
		selectedItem.set(item);
	}

	public S3Item getRootItem() {
		return rootItem;
	}

	public ObjectProperty<S3Item> selectedItemProperty() {
		return selectedItem;
	}

	private void selectedItemChanged(final ObservableValue<? extends S3Item> observable, final S3Item oldValue,
			final S3Item newValue) {
		if (newValue == null) {
			return;
		}
		ConcurrentUtil.run(() -> {
			if (AuthSetting.hasSetting()) {
				s3Service.updateItems(newValue);
			}
		});
	}

}
