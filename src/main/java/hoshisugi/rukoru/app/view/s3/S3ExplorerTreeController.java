package hoshisugi.rukoru.app.view.s3;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.s3.S3Item;
import hoshisugi.rukoru.framework.base.BaseController;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class S3ExplorerTreeController extends BaseController {

	@FXML
	private TreeView<S3Item> treeView;

	@Inject
	private S3ExplorerController explorer;

	@Override
	public void initialize(final URL url, final ResourceBundle resource) {
		treeView.setRoot(explorer.getRootItem().getTreeItem());
		treeView.getSelectionModel().selectedItemProperty().addListener(this::onTreeItemSelected);
		explorer.selectedItemProperty().addListener(this::selectedItemChanged);
	}

	private void selectedItemChanged(final ObservableValue<? extends S3Item> observable, final S3Item oldValue,
			final S3Item newValue) {
		if (newValue != null) {
			treeView.getSelectionModel().select(newValue.getTreeItem());
		} else {
			treeView.getSelectionModel().clearSelection();
		}
	}

	private void onTreeItemSelected(final ObservableValue<? extends TreeItem<S3Item>> observable,
			final TreeItem<S3Item> oldValue, final TreeItem<S3Item> newValue) {
		if (newValue != null) {
			explorer.setSelectedItem(newValue.getValue());
		}
	}

}
