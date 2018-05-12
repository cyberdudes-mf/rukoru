package hoshisugi.rukoru.app.view.settings;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.settings.Credential;
import hoshisugi.rukoru.app.services.settings.LocalSettingService;
import hoshisugi.rukoru.framework.annotations.FXController;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

@FXController(title = "Credential")
public class CredentialSettingController extends PreferenceControllerBase {

	@FXML
	private TextField account;

	@FXML
	private TextField accessKeyId;

	@FXML
	private TextField secretAccessKey;

	@Inject
	private LocalSettingService service;

	private Credential entity;

	@Override
	public void initialize(final URL url, final ResourceBundle resouce) {
		ConcurrentUtil.run(this::loadSetting);
	}

	private void loadSetting() {
		if (Credential.hasCredential()) {
			entity = Credential.get();
		} else {
			entity = new Credential();
		}
		account.textProperty().bindBidirectional(entity.accountProperty());
		accessKeyId.textProperty().bindBidirectional(entity.accessKeyIdProperty());
		secretAccessKey.textProperty().bindBidirectional(entity.secretAccessKeyProperty());
	}

	@Override
	public void apply() {
		ConcurrentUtil.run(() -> service.saveCredential(entity));
	}
}
