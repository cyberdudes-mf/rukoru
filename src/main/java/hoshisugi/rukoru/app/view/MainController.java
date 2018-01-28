package hoshisugi.rukoru.app.view;

import java.net.URL;
import java.util.ResourceBundle;

import hoshisugi.rukoru.app.view.settings.CredentialSettingController;
import hoshisugi.rukoru.app.view.settings.PreferencesController;
import hoshisugi.rukoru.app.view.settings.RepositoryDBSettingController;
import hoshisugi.rukoru.framework.annotations.FXController;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.util.BrowserUtil;
import hoshisugi.rukoru.framework.util.FXUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;

@FXController(title = "星杉☆るこる")
public class MainController extends BaseController {

	@FXML
	private MenuBar menuBar;

	@Override
	public void initialize(final URL url, final ResourceBundle resource) {
	}

	@FXML
	private void onClose(final ActionEvent event) {
		Platform.exit();
	}

	@FXML
	private void onHelp(final ActionEvent event) throws Exception {
		BrowserUtil.browse(/* TODO あとでURL決める */"http://www.google.com");
	}

	@FXML
	private void onAuthSetting(final ActionEvent event) throws Exception {
		FXUtil.popup(CredentialSettingController.class, FXUtil.getStage(event));
	}

	@FXML
	private void onRepositoryDBClick(final ActionEvent event) throws Exception {
		FXUtil.popup(RepositoryDBSettingController.class, FXUtil.getStage(event));
	}

	@FXML
	private void onPreferencesClick(final ActionEvent event) throws Exception {
		FXUtil.popup(PreferencesController.class, FXUtil.getStage(event));
	}
}
