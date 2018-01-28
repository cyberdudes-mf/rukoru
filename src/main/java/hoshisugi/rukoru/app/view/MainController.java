package hoshisugi.rukoru.app.view;

import java.net.URL;
import java.util.ResourceBundle;

import hoshisugi.rukoru.app.view.auth.AuthSettingController;
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
		BrowserUtil.browse(
				"http://redmine.dataspidercloud.tokyo/projects/dataspidercloud/wiki/%E6%98%9F%E6%9D%89%E2%98%86%E3%82%8B%E3%81%93%E3%82%8B");
	}

	@FXML
	private void onAuthSetting(final ActionEvent event) throws Exception {
		FXUtil.popup(AuthSettingController.class, FXUtil.getStage(event));
	}
}
