package hoshisugi.rukoru.app.view.settings;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.services.AuthService;
import hoshisugi.rukoru.flamework.annotations.FXController;
import hoshisugi.rukoru.flamework.controls.BaseController;
import hoshisugi.rukoru.flamework.util.FXUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

@FXController(title = "認証設定")
public class AuthSettingController extends BaseController {

	@FXML
	private TextField account;

	@FXML
	private TextField userName;

	@FXML
	private TextField password;

	@FXML
	private Button saveButton;

	@Inject
	private AuthService accountService;

	@Override
	public void initialize(final URL url, final ResourceBundle resouce) {
		saveButton.disableProperty().bind(account.textProperty().isEmpty().or(userName.textProperty().isEmpty())
				.or(password.textProperty().isEmpty()));
	}

	@FXML
	private void onSaveButtonClick(final ActionEvent event) {
		accountService.save(account.getText(), userName.getText(), password.getText());
		FXUtil.getStage(event).close();
	}

	@FXML
	private void onCancelButtonClick(final ActionEvent event) {
		FXUtil.getStage(event).close();
	}
}
