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
	private Button muteButton;

	@FXML
	private Slider volumeSlider;

	@FXML
	private Button fullScreenButton;

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
		playButton.setGraphic(new ImageView());
		playButton.setTooltip(new Tooltip());
		setPlayMode();
		stopButton.setGraphic(new ImageView(AssetUtil.getImage("16x16/stop.png")));
		stopButton.setTooltip(new Tooltip("Stop"));
		muteButton.setGraphic(new ImageView());
		muteButton.setTooltip(new Tooltip());
		setMuteMode();
		fullScreenButton.setGraphic(new ImageView(AssetUtil.getImage("16x16/full_screen.png")));
		fullScreenButton.setTooltip(new Tooltip("Full screen"));

		final Path path = Paths.get(
				// "G:/アニメ/[アニメ BD] 冴えない彼女の育てかた (1920x1080 x264 AAC オーディオコメンタリー切替可)/[アニメ BD]
				// 冴えない彼女の育てかた 1巻 映像特典 02／05 「ノンクレジットエンディング」 (1920x1080 x264 AAC).mp4");
				"G:/アニメ/[アニメ BD] 冴えない彼女の育てかた (1920x1080 x264 AAC オーディオコメンタリー切替可)/[アニメ BD] 冴えない彼女の育てかた 1巻 映像特典 01／05 「ノンクレジットオープニング」 (1920x1080 x264 AAC).mp4");

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
		if (playButton.getUserData() == PlayOrPause.Play) {
			mediaView.getMediaPlayer().play();
			setPauseMode();
		} else {
			mediaView.getMediaPlayer().pause();
			setPlayMode();
		}
	}

	@FXML
	private void onStopButtonClick(final ActionEvent event) {
		mediaView.getMediaPlayer().stop();
		setPlayMode();
	}

	@FXML
	private void onMuteButtonClick(final ActionEvent event) {
		final MediaPlayer player = mediaView.getMediaPlayer();
		if (muteButton.getUserData() == MuteOrUnmute.Mute) {
			player.setMute(true);
			setUnmuteMode();
		} else {
			player.setMute(false);
			setMuteMode();
		}
	}

	private void onEndOfMedia() {
		final MediaPlayer player = mediaView.getMediaPlayer();
		player.stop();
		player.play();
	}

	private void onReady() {
		final MediaPlayer player = mediaView.getMediaPlayer();
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
		playButton.setDisable(false);
		stopButton.setDisable(false);
		progressSlider.setDisable(false);
		muteButton.setDisable(false);
		volumeSlider.setDisable(false);
		fullScreenButton.setDisable(false);
	}

	private void onStopped() {
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
}
