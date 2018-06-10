package hoshisugi.rukoru.app.view.video;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.common.AsyncResult;
import hoshisugi.rukoru.app.models.settings.S3VideoCredential;
import hoshisugi.rukoru.app.models.video.VideoFile;
import hoshisugi.rukoru.app.models.video.VideoItem;
import hoshisugi.rukoru.app.services.video.VideoService;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.util.AssetUtil;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import hoshisugi.rukoru.framework.util.DialogUtil;
import hoshisugi.rukoru.framework.util.FXUtil;
import hoshisugi.rukoru.framework.util.IOUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

public class VideoContentsController extends BaseController {

	@FXML
	private TreeView<VideoItem> treeView;

	@FXML
	private TextField pathField;

	@FXML
	private Button openButton;

	@Inject
	private VideoController videoController;

	@Inject
	private VideoService videoService;

	private Path selection;

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		openButton.setGraphic(new ImageView(AssetUtil.getImage("16x16/folder.png")));
		treeView.setContextMenu(createContextMenu());
		treeView.setOnContextMenuRequested(this::onContextMenuRequested);
		if (S3VideoCredential.hasCredential()) {
			ConcurrentUtil.run(() -> loadS3Videos());
		}
	}

	private ContextMenu createContextMenu() {
		final ContextMenu contextMenu = new ContextMenu();
		final MenuItem item = new MenuItem("Download");
		item.setOnAction(this::onDownloadMenuSelected);
		contextMenu.getItems().add(item);
		return contextMenu;
	}

	private void loadS3Videos() {
		final VideoItem root = videoService.getVideos();
		final TreeItem<VideoItem> rootItem = root.toTreeItem();
		rootItem.setExpanded(true);
		Platform.runLater(() -> treeView.setRoot(rootItem));
	}

	@FXML
	private void onOpenButtonClick(final ActionEvent event) {
		final FileChooser chooser = new FileChooser();
		if (selection != null) {
			chooser.setInitialDirectory(selection.getParent().toFile());
		}
		final File file = chooser.showOpenDialog(FXUtil.getStage(event));
		if (file != null) {
			selection = file.toPath();
			pathField.setText(selection.getFileName().toString());
			videoController.loadVideo(selection);
		}
	}

	@FXML
	private void onMouseClicked(final MouseEvent event) {
		if (!FXUtil.isDoubleClicked(event)) {
			return;
		}
		final TreeItem<VideoItem> item = treeView.getSelectionModel().getSelectedItem();
		if (item.getValue() instanceof VideoFile) {
			final VideoFile video = (VideoFile) item.getValue();
			videoController.loadVideo(video.getUrl());
		}
	}

	private void onContextMenuRequested(final ContextMenuEvent e) {
		final TreeItem<VideoItem> selectedItem = treeView.getSelectionModel().getSelectedItem();
		treeView.getContextMenu().getItems().forEach(i -> i.setVisible(selectedItem.getValue().isFile()));
	}

	private void onDownloadMenuSelected(final ActionEvent e) {
		final VideoItem videoItem = treeView.getSelectionModel().getSelectedItem().getValue();
		if (!videoItem.isFile()) {
			return;
		}
		final VideoFile video = (VideoFile) videoItem;
		final Optional<File> destination = requireDownloadDestination(e, videoItem);
		destination.ifPresent(file -> {
			selection = file.toPath();
			try {
				final AsyncResult result = IOUtil.downloadContent(video.getUrl(), selection);
				videoController.showProgressBar(result);
			} catch (final IOException e1) {
				DialogUtil.showErrorDialog("エラー", video.getName() + "のダウンロードに失敗しました。", e1);
			}
		});
	}

	private Optional<File> requireDownloadDestination(final ActionEvent e, final VideoItem videoItem) {
		final FileChooser chooser = new FileChooser();
		chooser.setInitialFileName(videoItem.getName());
		if (selection != null) {
			chooser.setInitialDirectory(selection.getParent().toFile());
		}
		return Optional.ofNullable(chooser.showSaveDialog(FXUtil.getStage(e)));
	}

	public void refresh() {
		if (S3VideoCredential.hasCredential()) {
			ConcurrentUtil.run(() -> loadS3Videos());
		}
	}
}
