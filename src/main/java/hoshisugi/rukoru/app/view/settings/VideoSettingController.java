package hoshisugi.rukoru.app.view.settings;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.settings.S3VideoCredential;
import hoshisugi.rukoru.app.services.settings.LocalSettingService;
import hoshisugi.rukoru.framework.annotations.FXController;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

@FXController(title = "Video")
public class VideoSettingController extends PreferenceControllerBase {

	@FXML
	private TextField accessKeyId;

	@FXML
	private TextField secretAccessKey;

	@FXML
	private TextField bucket;

	@Inject
	private LocalSettingService settingService;

	private S3VideoCredential credential;

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		loadPreferences();
	}

	@Override
	public void apply() {
		ConcurrentUtil.run(() -> {
			settingService.saveS3VideoCredential(credential);
		});
	}

	private void loadPreferences() {
		ConcurrentUtil.run(() -> {
			if (S3VideoCredential.hasCredential()) {
				credential = S3VideoCredential.get();
			} else {
				credential = new S3VideoCredential();
			}
			Platform.runLater(() -> {
				accessKeyId.textProperty().bindBidirectional(credential.accessKeyIdProperty());
				secretAccessKey.textProperty().bindBidirectional(credential.secretAccessKeyProperty());
				bucket.textProperty().bindBidirectional(credential.bucketProperty());
			});
		});
	}

}
