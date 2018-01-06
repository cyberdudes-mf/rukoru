package hoshisugi.rukoru.app.view.s3;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.auth.AuthSetting;
import hoshisugi.rukoru.app.models.s3.S3Item;
import hoshisugi.rukoru.app.models.s3.S3Root;
import hoshisugi.rukoru.app.services.s3.S3Service;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;

public class S3ExplorerController extends BaseController {

	@FXML
	private SplitPane splitPane;

	@Inject
	private S3Service s3Service;

	private final ObjectProperty<S3Item> selectedItem = new SimpleObjectProperty<>(this, "selectedItem");

	private final S3Item rootItem = new S3Root();

	@Override
	public void initialize(final URL url, final ResourceBundle resource) {
		updateItem(rootItem);
		setSelectedItem(rootItem);
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

	private void updateItem(final S3Item item) {
		ConcurrentUtil.run(() -> {
			if (AuthSetting.hasSetting()) {
				s3Service.updateItems(item);
			}
		});
	}
}
