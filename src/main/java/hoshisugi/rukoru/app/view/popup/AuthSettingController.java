package hoshisugi.rukoru.app.view.popup;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.AuthSetting;
import hoshisugi.rukoru.app.services.auth.AuthService;
import hoshisugi.rukoru.flamework.annotations.FXController;
import hoshisugi.rukoru.flamework.controls.BaseController;
import hoshisugi.rukoru.flamework.util.ConcurrentUtil;
import hoshisugi.rukoru.flamework.util.DialogUtil;
import hoshisugi.rukoru.flamework.util.FXUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

@FXController(title = "認証設定")
public class AuthSettingController extends BaseController {

	@FXML
	private TextField account;

	@FXML
	private TextField accessKeyId;

	@FXML
	private TextField secretAccessKey;

	@FXML
	private Button saveButton;

	@Inject
	private AuthService authService;

	private AuthSetting entity;

	@Override
	public void initialize(final URL url, final ResourceBundle resouce) {
		saveButton.disableProperty().bind(account.textProperty().isEmpty().or(accessKeyId.textProperty().isEmpty())
				.or(secretAccessKey.textProperty().isEmpty()));
		ConcurrentUtil.run(this::loadSetting);
	}

	@FXML
	private void onSaveButtonClick(final ActionEvent event) {
		try {
			authService.save(entity);
		} catch (final SQLException e) {
			DialogUtil.showErrorDialog(e);
		}
		close(FXUtil.getStage(event));
	}

	@FXML
	private void onCancelButtonClick(final ActionEvent event) {
		close(FXUtil.getStage(event));
	}

	private void loadSetting() {
		try {
			entity = authService.load().orElseGet(AuthSetting::new);
		} catch (final UncheckedExecutionException e) {
			entity = new AuthSetting();
		}
		account.textProperty().bind(entity.accountProperty());
		accessKeyId.textProperty().bind(entity.accessKeyIdProperty());
		secretAccessKey.textProperty().bind(entity.secretAccessKeyProperty());
	}

	private void close(final Stage stage) {
		account.textProperty().unbind();
		accessKeyId.textProperty().unbind();
		secretAccessKey.textProperty().unbind();
		saveButton.disableProperty().unbind();
		stage.close();
	}
}
