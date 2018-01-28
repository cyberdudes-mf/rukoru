package hoshisugi.rukoru.app.view.settings;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.setings.Credential;
import hoshisugi.rukoru.app.services.settings.LocalSettingService;
import hoshisugi.rukoru.framework.annotations.FXController;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import hoshisugi.rukoru.framework.util.DialogUtil;
import hoshisugi.rukoru.framework.util.FXUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

@FXController(title = "認証設定")
public class CredentialSettingController extends BaseController {

	@FXML
	private TextField account;

	@FXML
	private TextField accessKeyId;

	@FXML
	private TextField secretAccessKey;

	@FXML
	private Button saveButton;

	@Inject
	private LocalSettingService service;

	private Credential entity;

	@Override
	public void initialize(final URL url, final ResourceBundle resouce) {
		saveButton.disableProperty().bind(account.textProperty().isEmpty().or(accessKeyId.textProperty().isEmpty())
				.or(secretAccessKey.textProperty().isEmpty()));
		ConcurrentUtil.run(this::loadSetting);
	}

	@FXML
	private void onSaveButtonClick(final ActionEvent event) {
		try {
			service.saveCredential(entity);
		} catch (final Exception e) {
			DialogUtil.showErrorDialog(e);
		}
		close(FXUtil.getStage(event));
	}

	@FXML
	private void onCancelButtonClick(final ActionEvent event) {
		close(FXUtil.getStage(event));
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

	private void close(final Stage stage) {
		account.textProperty().unbindBidirectional(entity.accountProperty());
		accessKeyId.textProperty().unbindBidirectional(entity.accessKeyIdProperty());
		secretAccessKey.textProperty().unbindBidirectional(entity.secretAccessKeyProperty());
		saveButton.disableProperty().unbind();
		stage.close();
	}
}
