package hoshisugi.rukoru.app.view;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.services.AuthService;
import hoshisugi.rukoru.app.services.AuthSetting;
import hoshisugi.rukoru.flamework.controls.BaseController;
import hoshisugi.rukoru.flamework.util.AssetUtil;
import hoshisugi.rukoru.flamework.util.BrowserUtil;
import hoshisugi.rukoru.flamework.util.DialogUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;

public class ToolBarController extends BaseController {

	@FXML
	private Button mcButton;

	@FXML
	private ToggleButton ec2Button;

	@FXML
	private ToggleButton s3Button;

	@Inject
	private AuthService authService;

	@Override
	public void initialize(final URL arg0, final ResourceBundle arg1) {
		mcButton.setGraphic(new ImageView(AssetUtil.getImage("MC.png", 30, 30)));
		ec2Button.setGraphic(new ImageView(AssetUtil.getImage("EC2.png", 30, 30)));
		s3Button.setGraphic(new ImageView(AssetUtil.getImage("S3.png", 30, 30)));
	}

	@FXML
	private void onMCButtonClicked(final ActionEvent event) throws Exception {
		final Optional<AuthSetting> authSetting = authService.load();
		if (authSetting.isPresent()) {
			BrowserUtil
					.browse(String.format("https://%s.signin.aws.amazon.com/console", authSetting.get().getAccount()));
		} else {
			DialogUtil.showWarningDialog("ムリ", "先に認証情報を設定してくれないと URL が分からないす。。。\n[メニュー] - [Settings] - [認証設定]");
		}
	}
}
