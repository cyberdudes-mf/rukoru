package hoshisugi.rukoru.app.view;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.enums.RukoruModule;
import hoshisugi.rukoru.app.models.settings.Credential;
import hoshisugi.rukoru.app.models.settings.Preference;
import hoshisugi.rukoru.app.services.settings.LocalSettingService;
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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class ToolBarController extends BaseController {

	@FXML
	private VBox layoutRoot;

	@Inject
	private LocalSettingService service;

	@Inject
	private ContentController contentController;

	private final ToggleGroup toolBar = new ToggleGroup();

	private final Map<RukoruModule, ButtonBase> buttons = new HashMap<>();

	@Override
	public void initialize(final URL url, final ResourceBundle resource) {
		createButtons();
		ConcurrentUtil.run(() -> {
			final Map<String, Preference> preferences = service.getPreferencesByCategory("Module");
			Platform.runLater(() -> showButtons(preferences));
		});
		toolBar.selectedToggleProperty().addListener(this::toolBarSelectionChanged);
	}

	@SuppressWarnings("incomplete-switch")
	private void createButtons() {
		for (final RukoruModule module : RukoruModule.values()) {
			ButtonBase button;
			if (module.getControllerClass() != null) {
				final ToggleButton toggleButton = new ToggleButton();
				toggleButton.setToggleGroup(toolBar);
				button = toggleButton;
				button.setUserData(module);
				button.setOnAction(this::onToggleButtonClick);
			} else {
				button = new Button();
				switch (module) {
				case ManagementConsole:
					button.setOnAction(this::onMCButtonClick);
					break;
				case TestPortal:
					button.setOnAction(this::onTestPortalButtonClick);
					break;
				case ClearCache:
					button.setOnAction(this::onMageButtonClick);
					break;
				}
			}
			button.setGraphic(new ImageView(AssetUtil.getImage(module.getIconPath())));
			button.setText(module.getDiplayName());
			button.setGraphicTextGap(10);
			button.setMinWidth(130);
			button.setAlignment(Pos.TOP_LEFT);
			buttons.put(module, button);
		}
	}

	public void refresh(final Map<String, Preference> preferences) {
		layoutRoot.getChildren().clear();
		showButtons(preferences);
	}

	private void onMCButtonClick(final ActionEvent event) {
		ConcurrentUtil.run(() -> {
			final Optional<Credential> authSetting = service.loadCredential();
			if (authSetting.isPresent()) {
				BrowserUtil.browse(
						String.format("https://%s.signin.aws.amazon.com/console", authSetting.get().getAccount()));
			} else {
				Platform.runLater(() -> DialogUtil.showWarningDialog(
						"認証情報を設定してください。\n[メニュー] - [Settings] - [Preferences] - [Authentication] - [認証設定]"));
			}
		});
	}

	private void onTestPortalButtonClick(final ActionEvent event) {
		BrowserUtil.browse(String.format("http://front.dataspidercloud.tokyo/"));
	}

	private void onMageButtonClick(final ActionEvent event) {
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

	private void onToggleButtonClick(final ActionEvent event) {
		final Node node = (Node) event.getTarget();
		contentController.showContent((RukoruModule) node.getUserData());
	}

	private void toolBarSelectionChanged(final ObservableValue<? extends Toggle> observable, final Toggle oldValue,
			final Toggle newValue) {
		if (newValue == null) {
			Platform.runLater(contentController::showHome);
		}
	}

	private void showButtons(final Map<String, Preference> preferences) {
		Stream.of(RukoruModule.values()).filter(m -> isActive(m, preferences)).map(buttons::get)
				.forEach(layoutRoot.getChildren()::add);
	}

	private boolean isActive(final RukoruModule rukoruModule, final Map<String, Preference> preferences) {
		final Preference preference = preferences.get(rukoruModule.toString());
		return preference == null || Boolean.parseBoolean(preference.getValue());
	}

	public boolean isSelected() {
		return toolBar.getSelectedToggle() != null;
	}
}
