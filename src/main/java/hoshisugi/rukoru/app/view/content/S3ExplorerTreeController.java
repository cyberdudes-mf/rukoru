package hoshisugi.rukoru.app.view.content;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.AuthSetting;
import hoshisugi.rukoru.app.models.S3Item;
import hoshisugi.rukoru.app.services.s3.S3Service;
import hoshisugi.rukoru.flamework.controls.BaseController;
import hoshisugi.rukoru.flamework.util.ConcurrentUtil;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeItem.TreeModificationEvent;
import javafx.scene.control.TreeView;

public class S3ExplorerTreeController extends BaseController {

	@FXML
	private TreeView<S3Item> treeView;

	@Inject
	private S3ExplorerController explorer;

	@Inject
	private S3Service s3Service;

	@Override
	public void initialize(final URL url, final ResourceBundle resource) {
		final TreeItem<S3Item> root = explorer.getSelectedItem().createTreeItem();
		root.addEventHandler(TreeItem.branchExpandedEvent(), this::onBranchExpanded);
		root.setExpanded(true);
		treeView.setRoot(root);
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

	private void onBranchExpanded(final TreeModificationEvent<S3Item> e) {
		final SelectionModel<TreeItem<S3Item>> selectionModel = treeView.getSelectionModel();
		final TreeItem<S3Item> selectedItem = selectionModel.getSelectedItem();
		final TreeItem<S3Item> treeItem = e.getSource();
		final S3Item s3Item = treeItem.getValue();
		ConcurrentUtil.run(() -> {
			if (AuthSetting.hasSetting()) {
				s3Service.updateItems(s3Item);
				final List<TreeItem<S3Item>> items = s3Item.getItems().stream().map(S3Item::createTreeItem)
						.collect(Collectors.toList());
				Platform.runLater(() -> {
					treeItem.getChildren().setAll(items);
					// setAll() の呼び出しによってツリーの選択アイテムが変わってしまうことがあるため、setAll() 後に再選択する
					selectionModel.select(selectedItem);
				});
			}
		});
	}

	private void onTreeItemSelected(final ObservableValue<? extends TreeItem<S3Item>> observable,
			final TreeItem<S3Item> oldValue, final TreeItem<S3Item> newValue) {
		if (newValue != null) {
			explorer.setSelectedItem(newValue.getValue());
		}
	}

}
