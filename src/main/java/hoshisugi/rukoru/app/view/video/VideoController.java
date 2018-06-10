package hoshisugi.rukoru.app.view.video;

import static java.lang.Double.MAX_VALUE;

import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import hoshisugi.rukoru.app.models.common.AsyncResult;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.util.AssetUtil;
import hoshisugi.rukoru.framework.util.DialogUtil;
import hoshisugi.rukoru.framework.util.FXUtil;
import javafx.application.Platform;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class VideoController extends BaseController {

	@FXML
	private VBox layoutRoot;

	@FXML
	private MediaView mediaView;

	@FXML
	private Button playButton;

	@FXML
	private Button stopButton;

	@FXML
	private Slider progressSlider;

	@FXML
	private Label progressText;

	@FXML
	private Button muteButton;

	@FXML
	private Slider volumeSlider;

	@FXML
	private Button fullScreenButton;

	@FXML
	private Button expandButton;

	@FXML
	private VBox bottom;

	private AnchorPane contentsView;

	private MediaPlayer player;

	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm:ss");
	private final StringProperty totalDuration = new SimpleStringProperty("00:00");
	private final StringProperty currentTime = new SimpleStringProperty("00:00");

	private enum PlayOrPause {
		Play, Pause;
	}

	private enum MuteOrUnmute {
		Mute, Unmute;
	}

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		initializeButtons();
		progressSlider.valueProperty().addListener(this::onProgressValueChanged);
		volumeSlider.valueProperty().addListener(this::onVolumeChanged);
	}

	private void initializeButtons() {
		playButton.setGraphic(new ImageView());
		playButton.setTooltip(new Tooltip());
		stopButton.setGraphic(new ImageView(AssetUtil.getImage("16x16/stop.png")));
		stopButton.setTooltip(new Tooltip("Stop"));
		muteButton.setGraphic(new ImageView());
		muteButton.setTooltip(new Tooltip());
		fullScreenButton.setGraphic(new ImageView(AssetUtil.getImage("16x16/full_screen.png")));
		fullScreenButton.setTooltip(new Tooltip("Full screen"));
		expandButton.setGraphic(new ImageView(AssetUtil.getImage("16x16/navigate_left.png")));
		setPlayMode();
		setMuteMode();
	}

	public void loadVideo(final Path path) {
		try {
			loadVideo(path.toUri().toURL());
		} catch (final MalformedURLException e) {
			throw new UncheckedIOException(e);
		}
	}

	public void loadVideo(final URL url) {
		if (player != null) {
			player.stop();
			player.dispose();
			onStopped();
			playButton.setDisable(true);
		}

		try {
			final Media media = new Media(url.toString());
			media.setOnError(() -> {
				Platform.runLater(() -> {
					DialogUtil.showWarningDialog("動画を読み込めませんでした。\n" + url.toString());
				});
			});
			player = new MediaPlayer(media);
			mediaView.setMediaPlayer(player);
		} catch (final MediaException e) {
			DialogUtil.showErrorDialog(e);
			return;
		}

		player.setOnReady(this::onReady);
		player.setOnPlaying(this::onPlaying);
		player.setOnPaused(this::onPaused);
		player.setOnStopped(this::onStopped);
		player.setOnEndOfMedia(this::onEndOfMedia);
		player.currentTimeProperty().addListener(this::onCurrentTimeChanged);
	}

	@FXML
	private void onPlayButtonClick(final ActionEvent event) {
		if (playButton.getUserData() == PlayOrPause.Play) {
			player.play();
			setPauseMode();
		} else {
			player.pause();
			setPlayMode();
		}
	}

	@FXML
	private void onStopButtonClick(final ActionEvent event) {
		player.stop();
	}

	@FXML
	private void onMuteButtonClick(final ActionEvent event) {
		if (muteButton.getUserData() == MuteOrUnmute.Mute) {
			player.setMute(true);
			setUnmuteMode();
		} else {
			player.setMute(false);
			setMuteMode();
		}
	}

	@FXML
	private void onFullScreenButtonClick(final ActionEvent event) {
		final Stage stage = FXUtil.getStage(event);
		final Scene scene = stage.getScene();
		final Parent origRoot = scene.getRoot();
		final Pane parent = (Pane) mediaView.getParent();
		final int origIndex = parent.getChildrenUnmodifiable().indexOf(mediaView);
		final double origFitWidth = mediaView.getFitWidth();
		final double origFitHeight = mediaView.getFitHeight();
		final StackPane root = new StackPane(mediaView);
		scene.setRoot(root);
		mediaView.fitWidthProperty().bind(root.widthProperty());
		mediaView.fitHeightProperty().bind(root.heightProperty());
		stage.setFullScreen(true);
		FXUtil.setOnChangedForOnce(stage.fullScreenProperty(), (observable, oldValue, newValue) -> {
			if (!newValue) {
				scene.setRoot(origRoot);
				final ObservableList<Node> children = parent.getChildren();
				if (!children.contains(mediaView)) {
					children.add(origIndex, mediaView);
					mediaView.fitWidthProperty().unbind();
					mediaView.fitHeightProperty().unbind();
					mediaView.setFitWidth(origFitWidth);
					mediaView.setFitHeight(origFitHeight);
				}
			}
		});
	}

	@FXML
	private void onExpandButtonClick(final ActionEvent event) {
		final Pane parent = (Pane) expandButton.getParent();
		final ObservableList<Node> children = parent.getChildren();
		if (children.contains(contentsView)) {
			children.remove(contentsView);
			expandButton.setGraphic(new ImageView(AssetUtil.getImage("16x16/navigate_left.png")));
		} else {
			if (contentsView == null) {
				contentsView = FXUtil.load(VideoContentsController.class);
				contentsView.setMaxWidth(300);
				contentsView.setMaxHeight(parent.getHeight() - expandButton.getHeight() - 5);
				StackPane.setAlignment(contentsView, Pos.BOTTOM_RIGHT);
			}
			children.add(contentsView);
			expandButton.setGraphic(new ImageView(AssetUtil.getImage("16x16/navigate_right.png")));
		}
	}

	private void onReady() {
		setPlayMode();
		playButton.setDisable(false);
		final Duration total = player.getTotalDuration();
		progressSlider.setMax(total.toSeconds());
		totalDuration.setValue(formatter.format(LocalTime.ofSecondOfDay((long) total.toSeconds())));
		progressText.textProperty().bind(new StringBinding() {

			{
				super.bind(totalDuration, currentTime);
			}

			@Override
			protected String computeValue() {
				return String.format("%s/%s", currentTime.getValue(), totalDuration.getValue());
			}
		});
	}

	private void onPlaying() {
		setPauseMode();
		stopButton.setDisable(false);
		progressSlider.setDisable(false);
		muteButton.setDisable(false);
		volumeSlider.setDisable(false);
		fullScreenButton.setDisable(false);
	}

	private void onPaused() {
		setPlayMode();
	}

	private void onStopped() {
		setPlayMode();
		stopButton.setDisable(true);
		progressSlider.setDisable(true);
		muteButton.setDisable(true);
		volumeSlider.setDisable(true);
		fullScreenButton.setDisable(true);
	}

	private void onCurrentTimeChanged(final ObservableValue<? extends Duration> observable, final Duration oldValue,
			final Duration newValue) {
		if (Math.abs(newValue.toSeconds() - progressSlider.getValue()) > 1.0) {
			progressSlider.setValue(newValue.toSeconds());
			currentTime.setValue(formatter.format(LocalTime.ofSecondOfDay((long) newValue.toSeconds())));
		}
	}

	private void onEndOfMedia() {
		player.stop();
	}

	private void onProgressValueChanged(final ObservableValue<? extends Number> observable, final Number oldValue,
			final Number newValue) {
		if (Math.abs(player.getCurrentTime().toSeconds() - newValue.doubleValue()) > 3.0) {
			player.seek(Duration.seconds(newValue.doubleValue()));
		}
	}

	private void onVolumeChanged(final ObservableValue<? extends Number> observable, final Number oldValue,
			final Number newValue) {
		player.setVolume(newValue.doubleValue());
	}

	private void setPlayMode() {
		final ImageView image = (ImageView) playButton.getGraphic();
		image.setImage(AssetUtil.getImage("16x16/run.png"));
		playButton.getTooltip().setText("Play");
		playButton.setUserData(PlayOrPause.Play);
	}

	private void setPauseMode() {
		final ImageView image = (ImageView) playButton.getGraphic();
		image.setImage(AssetUtil.getImage("16x16/pause.png"));
		playButton.getTooltip().setText("Pause");
		playButton.setUserData(PlayOrPause.Pause);
	}

	private void setMuteMode() {
		final ImageView image = (ImageView) muteButton.getGraphic();
		image.setImage(AssetUtil.getImage("16x16/mute.png"));
		muteButton.getTooltip().setText("Mute");
		muteButton.setUserData(MuteOrUnmute.Mute);
	}

	private void setUnmuteMode() {
		final ImageView image = (ImageView) muteButton.getGraphic();
		image.setImage(AssetUtil.getImage("16x16/volume.png"));
		muteButton.getTooltip().setText("Unmute");
		muteButton.setUserData(MuteOrUnmute.Unmute);
	}

	public void refreshContents() {
		if (contentsView == null) {
			return;
		}
		final VideoContentsController controller = (VideoContentsController) contentsView.getUserData();
		controller.refresh();
	}

	public void showProgressBar(final AsyncResult result) {
		final ProgressBar progressBar = createProgressBar(result);
		bottom.getChildren().add(progressBar);
		result.callback(() -> {
			bottom.getChildren().remove(progressBar);
			result.checkResult();
		});
	}

	private ProgressBar createProgressBar(final AsyncResult result) {
		final ProgressBar progressBar = new ProgressBar();
		progressBar.progressProperty().bind(result.progressProperty());
		progressBar.setPrefHeight(25);
		progressBar.setMaxWidth(MAX_VALUE);
		return progressBar;
	}
}
