package hoshisugi.rukoru.app.view.trace;

import static hoshisugi.rukoru.framework.util.ChooserUtil.XLSX;
import static hoshisugi.rukoru.framework.util.ChooserUtil.chooser;
import static hoshisugi.rukoru.framework.util.FXUtil.getStage;
import static javafx.scene.input.TransferMode.COPY;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.common.AsyncResult;
import hoshisugi.rukoru.app.services.excel.ExcelService;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import hoshisugi.rukoru.framework.util.DialogUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.layout.HBox;

public class ExcelTraceController extends BaseController {

	@FXML
	private HBox urlHbox;

	@FXML
	private TextField urlField;

	@FXML
	private Button loadButton;

	@FXML
	private ImageView imageView;

	@FXML
	private Button generateButton;

	@FXML
	private ProgressBar progressBar;

	@Inject
	private ExcelService service;

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		loadButton.disableProperty().bind(urlField.textProperty().isEmpty());
		generateButton.disableProperty().bind(imageView.imageProperty().isNull());
		loadImage();
	}

	@FXML
	private void onLoadButtonClick(final ActionEvent event) {
		loadImage();
	}

	@FXML
	private void onGenerateButtonClick(final ActionEvent event) {
		final Image image = imageView.getImage();
		if (!confirmIfLarge(image)) {
			return;
		}

		final Optional<Path> choose = chooser().extensions(XLSX).showSaveDialog(getStage(event));
		choose.ifPresent(p -> {
			progressBar.setVisible(true);
			generateButton.disableProperty().unbind();
			generateButton.setDisable(true);
			ConcurrentUtil.run(() -> {
				final AsyncResult result = service.trace(image, p);
				progressBar.progressProperty().bind(result.progressProperty());
				result.callback(() -> {
					if (result.checkResult()) {
						DialogUtil.showInfoDialog("完了", "トレースが完了しました。");
					}
					progressBar.setVisible(false);
					generateButton.disableProperty().bind(imageView.imageProperty().isNull());
				});
			});
		});
	}

	@FXML
	private void onDragOver(final DragEvent event) {
		final Dragboard dragboard = event.getDragboard();
		if (dragboard.hasFiles() && dragboard.getFiles().stream().filter(File::isFile).count() == 1) {
			event.acceptTransferModes(COPY);
		}
		event.consume();
	}

	@FXML
	private void onDragDropped(final DragEvent event) {
		final Dragboard dragboard = event.getDragboard();
		boolean success = false;
		if (dragboard.hasFiles()) {
			dragboard.getFiles().stream().filter(File::isFile).findFirst().ifPresent(f -> {
				ConcurrentUtil.run(() -> {
					final String url = f.toPath().toUri().toString();
					final Image image = new Image(url);
					Platform.runLater(() -> {
						urlField.setText(url);
						imageView.setImage(image);
					});
				});
			});
			success = true;
		}
		event.setDropCompleted(success);
		event.consume();
	}

	private void loadImage() {
		ConcurrentUtil.run(() -> {
			final Image image = new Image(urlField.getText());
			Platform.runLater(() -> imageView.setImage(image));
		});
	}

	private boolean confirmIfLarge(final Image image) {
		final int size = (int) image.getWidth() * (int) image.getHeight();
		if (size > 200 * 200) {
			final Optional<ButtonType> confirm = DialogUtil.showConfirmDialog("確認", "このサイズだとメッチャ時間かかりますけど、本当にやりますか？");
			return confirm.map(t -> t == ButtonType.OK).orElse(false);
		}
		return true;
	}

}
