package hoshisugi.rukoru.app.view.auth;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.auth.AuthSetting;
import hoshisugi.rukoru.app.services.auth.AuthService;
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
		} catch (final SQLException e) {
			entity = new AuthSetting();
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
