package hoshisugi.rukoru.app.view;

import java.net.URL;
import java.util.ResourceBundle;

import hoshisugi.rukoru.flamework.annotations.FXController;
import hoshisugi.rukoru.flamework.controls.ControllerBase;
import hoshisugi.rukoru.flamework.util.BrowserUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;

@FXController(title = "星杉☆るこる")
public class MainController extends ControllerBase {

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
	private void onLoginSetting(final ActionEvent event) throws Exception {
		// TODO 認証情報の設定画面出す
	}
}
