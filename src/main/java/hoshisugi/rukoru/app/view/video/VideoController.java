package hoshisugi.rukoru.app.view.video;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.util.AssetUtil;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
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
	private Button volumeButton;

	@FXML
	private Slider volumeSlider;

	@FXML
	private Button fullScreenButton;

	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm:ss");
	private final StringProperty totalDuration = new SimpleStringProperty("00:00");
	private final StringProperty currentTime = new SimpleStringProperty("00:00");

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		playButton.setGraphic(new ImageView(AssetUtil.getImage("16x16/run.png")));
		playButton.setTooltip(new Tooltip("Play"));
		stopButton.setGraphic(new ImageView(AssetUtil.getImage("16x16/stop.png")));
		stopButton.setTooltip(new Tooltip("Stop"));
		fullScreenButton.setGraphic(new ImageView(AssetUtil.getImage("16x16/selection.png")));
		fullScreenButton.setTooltip(new Tooltip("Full screen"));

		final Path path = Paths.get("【ここに動画ファイルのパスを指定する】");

		final Media media = new Media(path.toUri().toString());
		final MediaPlayer player = new MediaPlayer(media);
		mediaView.setMediaPlayer(player);
		mediaView.fitWidthProperty().bind(layoutRoot.widthProperty());
		player.setOnEndOfMedia(this::onEndOfMedia);
		player.setOnReady(this::onReady);
		player.setOnPlaying(this::onPlaying);
		player.setOnStopped(this::onStopped);
		player.currentTimeProperty().addListener(this::onCurrentTimeChanged);
		progressSlider.valueProperty().addListener(this::onProgressValueChanged);
		volumeSlider.valueProperty().addListener(this::onVolumeChanged);
	}

	@FXML
	private void onPlayButtonClick(final ActionEvent event) {
		mediaView.getMediaPlayer().play();
	}

	@FXML
	private void onStopButtonClick(final ActionEvent event) {
		mediaView.getMediaPlayer().stop();
	}

	private void onEndOfMedia() {
		final MediaPlayer player = mediaView.getMediaPlayer();
		player.stop();
		player.play();
	}

	private void onReady() {
		final MediaPlayer player = mediaView.getMediaPlayer();
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
		playButton.setDisable(false);
		stopButton.setDisable(false);
		progressSlider.setDisable(false);
		volumeButton.setDisable(false);
		volumeSlider.setDisable(false);
		fullScreenButton.setDisable(false);
	}

	private void onStopped() {
		playButton.setDisable(false);
		stopButton.setDisable(true);
		progressSlider.setDisable(true);
		volumeButton.setDisable(true);
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

	private void onProgressValueChanged(final ObservableValue<? extends Number> observable, final Number oldValue,
			final Number newValue) {
		final MediaPlayer player = mediaView.getMediaPlayer();
		if (Math.abs(player.getCurrentTime().toSeconds() - newValue.doubleValue()) > 3.0) {
			player.seek(Duration.seconds(newValue.doubleValue()));
		}
	}

	private void onVolumeChanged(final ObservableValue<? extends Number> observable, final Number oldValue,
			final Number newValue) {
		final MediaPlayer player = mediaView.getMediaPlayer();
		player.setVolume(newValue.doubleValue());
	}
}
