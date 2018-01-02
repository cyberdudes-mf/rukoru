package hoshisugi.rukoru.app.view.content;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.AuthSetting;
import hoshisugi.rukoru.app.models.S3Bucket;
import hoshisugi.rukoru.app.models.S3Item;
import hoshisugi.rukoru.app.services.ec2.S3Service;
import hoshisugi.rukoru.flamework.controls.BaseController;
import hoshisugi.rukoru.flamework.util.ConcurrentUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class S3ExplorerTreeController extends BaseController {

	@FXML
	private TreeView<S3Item> treeView;

	@Inject
	private S3ExplorerController ecplorer;

	@Inject
	private S3Service s3Service;

	@Override
	public void initialize(final URL url, final ResourceBundle resource) {
		final TreeItem<S3Item> root = ecplorer.getSelectedItem().toTreeItem();
		root.setExpanded(true);
		treeView.setRoot(root);
		ConcurrentUtil.run(() -> {
			if (AuthSetting.hasSetting()) {
				final List<S3Bucket> buckets = s3Service.listBuckets();
				final List<TreeItem<S3Item>> items = buckets.stream().map(S3Item::toTreeItem)
						.collect(Collectors.toList());
				Platform.runLater(() -> treeView.getRoot().getChildren().addAll(items));
			}
		});
	}

}
