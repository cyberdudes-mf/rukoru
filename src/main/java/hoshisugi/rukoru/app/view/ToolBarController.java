package hoshisugi.rukoru.app.view;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.settings.Credential;
import hoshisugi.rukoru.app.services.settings.LocalSettingService;
import hoshisugi.rukoru.app.view.ds.DSContentController;
import hoshisugi.rukoru.app.view.ec2.EC2ContentController;
import hoshisugi.rukoru.app.view.repositorydb.RepositoryDBContentController;
import hoshisugi.rukoru.app.view.s3.S3ExplorerController;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.cli.CLI;
import hoshisugi.rukoru.framework.cli.CLIState;
import hoshisugi.rukoru.framework.util.AssetUtil;
import hoshisugi.rukoru.framework.util.BrowserUtil;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import hoshisugi.rukoru.framework.util.DialogUtil;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;

public class ToolBarController extends BaseController {

	@FXML
	private Button mcButton;

	@FXML
	private Button testPortalButton;

	@FXML
	private Button mageButton;

	@FXML
	private ToggleButton dsButton;

	@FXML
	private ToggleButton ec2Button;

	@FXML
	private ToggleButton repositoryDBButton;

	@FXML
	private ToggleButton s3Button;

	@FXML
	private ToggleGroup toolBar;

	@Inject
	private LocalSettingService authService;

	@Inject
	private ContentController contentController;

	@Override
	public void initialize(final URL url, final ResourceBundle resource) {
		mcButton.setGraphic(new ImageView(AssetUtil.getImage("32x32/AWS.png")));
		testPortalButton.setGraphic(new ImageView(AssetUtil.getImage("32x32/DS.png")));
		mageButton.setGraphic(new ImageView(AssetUtil.getImage("32x32/WPF.png")));
		dsButton.setGraphic(new ImageView(AssetUtil.getImage("32x32/DS.png")));
		ec2Button.setGraphic(new ImageView(AssetUtil.getImage("32x32/EC2.png")));
		repositoryDBButton.setGraphic(new ImageView(AssetUtil.getImage("32x32/DB.png")));
		s3Button.setGraphic(new ImageView(AssetUtil.getImage("32x32/S3.png")));
		toolBar.selectedToggleProperty().addListener(this::toolBarSelectionChanged);
	}

	@FXML
	private void onMCButtonClick(final ActionEvent event) throws Exception {
		final Optional<Credential> authSetting = authService.loadCredential();
		if (authSetting.isPresent()) {
			BrowserUtil
					.browse(String.format("https://%s.signin.aws.amazon.com/console", authSetting.get().getAccount()));
		} else {
			DialogUtil.showWarningDialog("認証情報を設定してください。\n[メニュー] - [Settings] - [認証設定]");
		}
	}

	@FXML
	private void onTestPortalButtonClick(final ActionEvent event) throws Exception {
		BrowserUtil.browse(String.format("http://front.dataspidercloud.tokyo/"));
	}

	@FXML
	private void onMageButtonClick(final ActionEvent event) throws Exception {
		ConcurrentUtil.run(() -> {
			final Path target = Paths.get(System.getProperty("user.home")).resolve("AppData/Local/Apps/2.0");
			if (!Files.exists(target)) {
				Platform.runLater(() -> DialogUtil.showInfoDialog("Clear Cache", "キャッシュはありません。\n" + target));
				return;
			}
			try (final CLIState state = CLI.command("rd").options("/s", "/q", target.toString()).execute()) {
				state.waitFor();
				if (state.isSuccess()) {
					Platform.runLater(() -> DialogUtil.showInfoDialog("Clear Cache", "キャッシュをクリアしました。\n" + target));
				} else {
					Platform.runLater(
							() -> DialogUtil.showWarningDialog("Clear Cache", "キャッシュをクリアできませんでした。\n" + target));
				}
			}
		});
	}

	@FXML
	private void onDSButtonClick(final ActionEvent event) {
		contentController.showContent(DSContentController.class);
	}

	@FXML
	private void onEC2ButtonClick(final ActionEvent event) {
		contentController.showContent(EC2ContentController.class);
	}

	@FXML
	private void onS3ButtonCLick(final ActionEvent event) {
		contentController.showContent(S3ExplorerController.class);
	}

	@FXML
	private void onRepositoryDBButtonCLick(final ActionEvent event) {
		contentController.showContent(RepositoryDBContentController.class);
	}

	private void toolBarSelectionChanged(final ObservableValue<? extends Toggle> observable, final Toggle oldValue,
			final Toggle newValue) {
		if (newValue == null) {
			Platform.runLater(contentController::showHome);
		}
	}

	public boolean isSelected() {
		return toolBar.getSelectedToggle() != null;
	}
}
