package hoshisugi.rukoru.app.view.video;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.settings.S3VideoCredential;
import hoshisugi.rukoru.app.models.video.VideoFile;
import hoshisugi.rukoru.app.models.video.VideoItem;
import hoshisugi.rukoru.app.services.video.VideoService;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.util.AssetUtil;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import hoshisugi.rukoru.framework.util.FXUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
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
		if (S3VideoCredential.hasCredential()) {
			ConcurrentUtil.run(() -> loadS3Videos());
		}
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

	public void refresh() {
		if (S3VideoCredential.hasCredential()) {
			ConcurrentUtil.run(() -> loadS3Videos());
		}
	}
}
