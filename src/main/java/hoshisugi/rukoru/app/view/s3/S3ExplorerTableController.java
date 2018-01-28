package hoshisugi.rukoru.app.view.s3;

import static hoshisugi.rukoru.app.models.s3.AsyncResult.Status.Done;
import static hoshisugi.rukoru.app.models.s3.S3Item.DELIMITER;
import static hoshisugi.rukoru.app.models.s3.S3Item.Type.Root;
import static java.lang.Double.MAX_VALUE;
import static javafx.scene.input.TransferMode.COPY;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import com.google.inject.Inject;
import com.sun.javafx.scene.control.skin.TableColumnHeader;

import hoshisugi.rukoru.app.models.s3.AsyncResult;
import hoshisugi.rukoru.app.models.s3.S3Bucket;
import hoshisugi.rukoru.app.models.s3.S3Clipboard;
import hoshisugi.rukoru.app.models.s3.S3Folder;
import hoshisugi.rukoru.app.models.s3.S3Item;
import hoshisugi.rukoru.app.models.s3.S3Root;
import hoshisugi.rukoru.app.models.s3.UploadObjectResult;
import hoshisugi.rukoru.app.models.settings.Credential;
import hoshisugi.rukoru.app.services.s3.S3Service;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.controls.GraphicTableCell;
import hoshisugi.rukoru.framework.util.BrowserUtil;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import hoshisugi.rukoru.framework.util.DialogUtil;
import hoshisugi.rukoru.framework.util.FXUtil;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@SuppressWarnings("restriction")
public class S3ExplorerTableController extends BaseController {

	@FXML
	private TableView<S3Item> tableView;

	@FXML
	private TableColumn<S3Item, S3Item> iconColumn;

	@FXML
	private TableColumn<S3Item, String> nameColumn;

	@FXML
	private TableColumn<S3Item, String> lastModifiedColumn;

	@FXML
	private TableColumn<S3Item, String> sizeColumn;

	@FXML
	private TableColumn<S3Item, String> storageClassColumn;

	@Inject
	private S3ExplorerController explorer;

	@Inject
	private S3Service s3Service;

	@Override
	public void initialize(final URL url, final ResourceBundle resource) {
		iconColumn.setCellValueFactory(GraphicTableCell.forTableCellValueFactory());
		iconColumn.setCellFactory(GraphicTableCell.forTableCellFactory(this::createIcon));
		explorer.getSelection().selectedItemProperty().addListener(this::selectedItemChanged);
		tableView.setRowFactory(this::createTableRow);
		tableView.setContextMenu(createContextMenu());
		tableView.setOnContextMenuRequested(this::onContextMenuRequested);
		tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}

	@FXML
	private void onDragOver(final DragEvent event) {
		final Dragboard dragboard = event.getDragboard();
		final S3Item selectedItem = explorer.getSelection().getSelectedItem();
		if (dragboard.hasFiles() && dragboard.getFiles().stream().allMatch(File::isFile)
				&& selectedItem.getType() != Root) {
			event.acceptTransferModes(COPY);
		}
		event.consume();
	}

	@FXML
	private void onDragDropped(final DragEvent event) {
		final Dragboard dragboard = event.getDragboard();
		boolean success = false;
		if (dragboard.hasFiles()) {
			final List<File> files = dragboard.getFiles();
			final S3Item parent = explorer.getSelection().getSelectedItem();
			files.forEach(f -> uploadFile(parent, f));
			success = true;
		}
		event.setDropCompleted(success);
		event.consume();
	}

	private void selectedItemChanged(final ObservableValue<? extends S3Item> observable, final S3Item oldValue,
			final S3Item newValue) {
		if (newValue != null) {
			tableView.setItems(newValue.getItems());
		} else {
			tableView.getItems().clear();
		}
	}

	private ImageView createIcon(final S3Item item) {
		return new ImageView(item.getIcon());
	}

	private TableRow<S3Item> createTableRow(final TableView<S3Item> tableView) {
		final TableRow<S3Item> row = new TableRow<>();
		final S3ContextMenu contextMenu = createContextMenu();
		contextMenu.userDataProperty().bind(row.itemProperty());
		row.setContextMenu(contextMenu);
		row.setOnContextMenuRequested(e -> contextMenu.disableItems(row.getItem()));
		row.setOnMouseClicked(this::onTableViewClicked);
		return row;
	}

	private void onTableViewClicked(final MouseEvent event) {
		if (FXUtil.isDoubleClicked(event)) {
			@SuppressWarnings("unchecked")
			final TableRow<S3Item> row = (TableRow<S3Item>) event.getSource();
			final S3Item item = row.getItem();
			if (item != null && item.isContainer()) {
				explorer.getSelection().select(item);
			}
		}
	}

	private S3ContextMenu createContextMenu() {
		final S3ContextMenu contextMenu = new S3ContextMenu();
		contextMenu.setOnOpenMenuAction(this::onOpenMenuAction);
		contextMenu.setOnUploadMenuAction(this::onUploadMenuAction);
		contextMenu.setOnDownloadMenuAction(this::onDownloadMenuAction);
		contextMenu.setOnCreateBucketMenuAction(this::onCreateBucketMenuAction);
		contextMenu.setOnCreateFolderMenuAction(this::onCreateFolderMenuAction);
		contextMenu.setOnRenameMenuAction(this::onRenameMenuAction);
		contextMenu.setOnDeleteMenuAction(this::onDeleteMenuAction);
		contextMenu.setOnCutMenuAction(this::onCutMenuAction);
		contextMenu.setOnCopyMenuAction(this::onCopyMenuAction);
		contextMenu.setOnPasteMenuAction(this::onPasteMenuAction);
		contextMenu.setOnPublishMenuAction(this::onPublishMenuAction);
		return contextMenu;
	}

	private void onContextMenuRequested(final ContextMenuEvent event) {
		final S3ContextMenu contextMenu = (S3ContextMenu) tableView.getContextMenu();
		if (isTableHeaderChild((Node) event.getTarget())) {
			contextMenu.setVisible(false);
		} else {
			contextMenu.disableItems(null);
		}
	}

	private void onOpenMenuAction(final ActionEvent event) {
		final S3Item item = getS3Item((MenuItem) event.getTarget());
		if (item.isContainer()) {
			explorer.getSelection().select(item);
		} else {
			BrowserUtil.browse(toURL(item));
		}
	}

	public void onUploadMenuAction(final ActionEvent event) {
		if (!checkAuth()) {
			return;
		}
		final S3Item parent = explorer.getSelection().getSelectedItem();
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("ダウンロード");
		final File selectedFile = fileChooser.showOpenDialog(FXUtil.getStage(event));
		if (selectedFile != null) {
			uploadFile(parent, selectedFile);
		}
	}

	private void uploadFile(final S3Item parent, final File selectedFile) {
		try {
			final String key = parent.getKey() + selectedFile.getName();
			final UploadObjectResult result = s3Service.uploadObject(parent.getBucketName(), key,
					selectedFile.toPath());
			final ProgressBar progressBar = createProgressBar(result);
			explorer.addBottom(progressBar);
			ConcurrentUtil.run(() -> {
				waitForDone(result);
				Platform.runLater(() -> {
					explorer.removeBottom(progressBar);
					if (result.checkResult()) {
						final S3Item item = result.getItem();
						final ObservableList<S3Item> items = parent.getItems();
						if (items.contains(item)) {
							items.remove(items.indexOf(item));
						}
						items.add(item);
						tableView.refresh();
					}
				});
			});
		} catch (final Exception e) {
			DialogUtil.showErrorDialog(e);
		}
	}

	private void onDownloadMenuAction(final ActionEvent event) {
		if (!checkAuth()) {
			return;
		}
		final S3Item item = getS3Item((MenuItem) event.getTarget());
		final FileChooser fileChooser = createFileChooser("ダウンロード");
		fileChooser.initialFileNameProperty().bind(item.nameProperty());
		final File selectedFile = fileChooser.showSaveDialog(FXUtil.getStage(event));
		if (selectedFile != null) {
			try {
				final AsyncResult result = s3Service.downloadObject(item, selectedFile.toPath());
				final ProgressBar progressBar = createProgressBar(result);
				explorer.addBottom(progressBar);
				ConcurrentUtil.run(() -> {
					waitForDone(result);
					Platform.runLater(() -> {
						explorer.removeBottom(progressBar);
						result.checkResult();
					});
				});
			} catch (final Exception e) {
				DialogUtil.showErrorDialog(e);
			}
		}
	}

	private void waitForDone(final AsyncResult result) throws InterruptedException {
		while (result.getStatus() != Done) {
			Thread.sleep(1000);
		}
	}

	private ProgressBar createProgressBar(final AsyncResult result) {
		final ProgressBar progressBar = new ProgressBar();
		progressBar.progressProperty().bind(result.progressProperty());
		progressBar.setPrefHeight(25);
		progressBar.setMaxWidth(MAX_VALUE);
		return progressBar;
	}

	private FileChooser createFileChooser(final String title) {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(title);
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt"),
				new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
				new ExtensionFilter("HTML Files", "*.html", "*.htm"),
				new ExtensionFilter("Archive Files", "*.zip", ".gz", ".tar"), new ExtensionFilter("All Files", "*.*"));
		return fileChooser;
	}

	private void onCreateBucketMenuAction(final ActionEvent event) {
		if (!checkAuth()) {
			return;
		}
		final MenuItem menuItem = (MenuItem) event.getTarget();
		final Optional<String> bucketNameOptional = DialogUtil.showTextInputDialog(menuItem.getText(), "バケット名");
		if (bucketNameOptional.isPresent()) {
			ConcurrentUtil.run(() -> {
				final S3Bucket bucket = s3Service.createBucket(bucketNameOptional.get());
				final S3Root rootItem = explorer.getRootItem();
				Platform.runLater(() -> {
					rootItem.getItems().add(bucket);
					clearAndSelect(bucket);
				});
			});
		}
	}

	private void onCreateFolderMenuAction(final ActionEvent event) {
		if (!checkAuth()) {
			return;
		}
		final MenuItem menuItem = (MenuItem) event.getTarget();
		final S3Item item = explorer.getSelection().getSelectedItem();
		final Optional<String> folderNameOptional = DialogUtil.showTextInputDialog(menuItem.getText(), "フォルダ名");
		if (folderNameOptional.isPresent()) {
			ConcurrentUtil.run(() -> {
				final String key = item.getKey() + folderNameOptional.get() + DELIMITER;
				final S3Folder folder = s3Service.createFolder(item.getBucketName(), key);
				Platform.runLater(() -> {
					item.getItems().add(folder);
					clearAndSelect(folder);
				});
			});
		}
	}

	private void onRenameMenuAction(final ActionEvent event) {
		if (!checkAuth()) {
			return;
		}
		final MenuItem menuItem = (MenuItem) event.getTarget();
		final S3Item item = getS3Item(menuItem);
		final Optional<String> nameOptional = DialogUtil.showTextInputDialog(menuItem.getText(), "名前", item.getName());
		if (nameOptional.isPresent()) {
			final String bucketName = item.getBucketName();
			final String sourceKey = item.getKey();
			final String destinationKey = item.getParentKey() + nameOptional.get();
			if (!destinationKey.equals(sourceKey)) {
				ConcurrentUtil.run(() -> {
					final ObservableList<S3Item> parentItems = item.getParent().getItems();
					final S3Item newItem = s3Service.moveObject(bucketName, sourceKey, bucketName, destinationKey);
					parentItems.remove(item);
					parentItems.add(newItem);
					Platform.runLater(() -> {
						clearAndSelect(newItem);
					});
				});
			}
		}
	}

	private void onDeleteMenuAction(final ActionEvent event) {
		if (!checkAuth()) {
			return;
		}
		final List<S3Item> items = tableView.getSelectionModel().getSelectedItems();
		if (!confirmDeleteObjects(items)) {
			return;
		}
		ConcurrentUtil.run(() -> {
			for (final S3Item item : items) {
				if (item instanceof S3Bucket) {
					s3Service.deleteBucket((S3Bucket) item);
				} else {
					s3Service.deleteObject(item);
				}
			}
			final S3Item parent = explorer.getSelection().getSelectedItem();
			Platform.runLater(() -> {
				parent.getItems().removeAll(items);
				tableView.getSelectionModel().clearSelection();
			});
		});
	}

	private boolean confirmDeleteObjects(final List<S3Item> items) {
		final Optional<ButtonType> buttonType;
		if (items.size() == 1) {
			buttonType = DialogUtil.showConfirmDialog("確認", String.format("[%s] を削除しますか？", items.get(0).getName()));
		} else {
			buttonType = DialogUtil.showConfirmDialog("確認", String.format("%s 個のファイルを削除しますか？", items.size()));
		}
		return buttonType.map(t -> t == ButtonType.OK).orElse(false);
	}

	private void onCutMenuAction(final ActionEvent event) {
		S3Clipboard.cut(getS3Item((MenuItem) event.getTarget()));
	}

	private void onCopyMenuAction(final ActionEvent event) {
		S3Clipboard.copy(getS3Item((MenuItem) event.getTarget()));
	}

	private void onPasteMenuAction(final ActionEvent event) {
		if (!checkAuth()) {
			return;
		}
		S3Clipboard.getContent().ifPresent(content -> {
			final S3Item parent = explorer.getSelection().getSelectedItem();
			final String destinationKey = parent.getKey() + content.getName();
			ConcurrentUtil.run(() -> {
				if (content.isCopied()) {
					final S3Item item = s3Service.copyObject(content.getBucketName(), content.getKey(),
							parent.getBucketName(), destinationKey);
					Platform.runLater(() -> {
						parent.getItems().add(item);
						clearAndSelect(item);
					});
				} else if (content.isCut()) {
					final S3Item item = s3Service.moveObject(content.getBucketName(), content.getKey(),
							parent.getBucketName(), destinationKey);
					final S3Item source = explorer.getRootItem().find(content.getBucketName(), content.getKey());
					Platform.runLater(() -> {
						parent.getItems().add(item);
						source.getParent().getItems().remove(source);
						S3Clipboard.clear();
						clearAndSelect(item);
					});
				}
			});
		});
	}

	private void onPublishMenuAction(final ActionEvent event) {
		if (!checkAuth()) {
			return;
		}
		final S3Item item = getS3Item((MenuItem) event.getTarget());
		ConcurrentUtil.run(() -> s3Service.publishObject(item));
	}

	private String toURL(final S3Item item) {
		final Escaper escaper = UrlEscapers.urlFragmentEscaper();
		final String bucketName = escaper.escape(item.getBucketName());
		final String key = (item.getKey() != null) ? escaper.escape(item.getKey()) : "";
		return String.format("https://s3-ap-northeast-1.amazonaws.com/%s/%s", bucketName, key);
	}

	private S3Item getS3Item(final MenuItem menuItem) {
		final S3ContextMenu contextMenu = (S3ContextMenu) menuItem.getParentPopup();
		return contextMenu.getUserData();
	}

	private boolean isTableHeaderChild(final Node node) {
		Node n = node;
		while (n != null) {
			if (n instanceof TableColumnHeader) {
				return true;
			}
			n = n.getParent();
		}
		return false;
	}

	private boolean checkAuth() {
		final boolean hasSetting = Credential.hasCredential();
		if (!hasSetting) {
			DialogUtil.showWarningDialog("認証情報を設定してください。\n[メニュー] - [Settings] - [認証設定]");
		}
		return hasSetting;
	}

	private void clearAndSelect(final S3Item newItem) {
		final TableViewSelectionModel<S3Item> selectionModel = tableView.getSelectionModel();
		selectionModel.clearSelection();
		selectionModel.select(newItem);
	}

}
