package hoshisugi.rukoru.app.view.s3;

import hoshisugi.rukoru.app.models.s3.S3Item;
import hoshisugi.rukoru.framework.inject.Injector;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class S3ContextMenu extends ContextMenu {

	private final MenuItem open = new MenuItem("開く");

	private final MenuItem upload = new MenuItem("アップロード");

	private final MenuItem download = new MenuItem("ダウンロード");

	private final MenuItem createBucket = new MenuItem("バケットの作成");

	private final MenuItem createFolder = new MenuItem("フォルダの作成");

	private final MenuItem rename = new MenuItem("名前の変更");

	private final MenuItem delete = new MenuItem("削除");

	private final MenuItem cut = new MenuItem("切り取る");

	private final MenuItem copy = new MenuItem("コピー");

	private final MenuItem paste = new MenuItem("貼り付ける");

	private final MenuItem publish = new MenuItem("公開する");

	private final ObjectProperty<S3Item> userData = new SimpleObjectProperty<>(this, "userData");

	public S3ContextMenu() {
		super();
		getItems().addAll(open, upload, download, new SeparatorMenuItem(), createBucket, createFolder, rename, delete,
				new SeparatorMenuItem(), cut, copy, paste, new SeparatorMenuItem(), publish);
	}

	public void disableItems(final S3Item item) {
		setVisible(true);
		final S3ExplorerController explorer = Injector.getInstance(S3ExplorerController.class);
		final boolean isRoot = explorer.getSelection().getSelectedItem().getType() == S3Item.Type.Root;
		open.setDisable(item == null);
		upload.setDisable(isRoot || (item != null && !item.isContainer()));
		download.setDisable(item == null || item.isContainer());
		createBucket.setVisible(isRoot);
		createFolder.setVisible(!isRoot);
		rename.setDisable(item == null || item.isContainer());
		delete.setDisable(item == null);
		cut.setDisable(item == null || isRoot);
		copy.setDisable(item == null || isRoot);
		paste.setDisable(/* TODO */false);
		publish.setDisable(item == null);
	}

	public void setOnOpenMenuAction(final EventHandler<ActionEvent> handler) {
		open.setOnAction(handler);
	}

	public void setOnUploadMenuAction(final EventHandler<ActionEvent> handler) {
		upload.setOnAction(handler);
	}

	public void setOnDownloadMenuAction(final EventHandler<ActionEvent> handler) {
		download.setOnAction(handler);
	}

	public void setOnCreateBucketMenuAction(final EventHandler<ActionEvent> handler) {
		createBucket.setOnAction(handler);
	}

	public void setOnCreateFolderMenuAction(final EventHandler<ActionEvent> handler) {
		createFolder.setOnAction(handler);
	}

	public void setOnRenameMenuAction(final EventHandler<ActionEvent> handler) {
		rename.setOnAction(handler);
	}

	public void setOnDeleteMenuAction(final EventHandler<ActionEvent> handler) {
		delete.setOnAction(handler);
	}

	public void setOnCutMenuAction(final EventHandler<ActionEvent> handler) {
		cut.setOnAction(handler);
	}

	public void setOnCopyMenuAction(final EventHandler<ActionEvent> handler) {
		copy.setOnAction(handler);
	}

	public void setOnPasteMenuAction(final EventHandler<ActionEvent> handler) {
		paste.setOnAction(handler);
	}

	public void setOnPublishMenuAction(final EventHandler<ActionEvent> handler) {
		publish.setOnAction(handler);
	}

	public ObjectProperty<S3Item> userDataProperty() {
		return userData;
	}

	@Override
	public S3Item getUserData() {
		return userData.get();
	}

	public void setVisible(final boolean visible) {
		getItems().forEach(i -> i.setVisible(visible));
	}
}
