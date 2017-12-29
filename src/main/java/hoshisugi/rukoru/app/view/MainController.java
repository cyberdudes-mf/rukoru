package hoshisugi.rukoru.app.view;

import java.net.URL;
import java.util.ResourceBundle;

import hoshisugi.rukoru.app.view.settings.AuthSettingController;
import hoshisugi.rukoru.flamework.annotations.FXController;
import hoshisugi.rukoru.flamework.controls.BaseController;
import hoshisugi.rukoru.flamework.util.BrowserUtil;
import hoshisugi.rukoru.flamework.util.FXUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Window;

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
		final MenuItem item = (MenuItem) event.getSource();
		final Window window = item.getStyleableNode().getScene().getWindow();
		FXUtil.popup(AuthSettingController.class, window);
	}
}
