package hoshisugi.rukoru.app.view.settings;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import com.google.common.base.Strings;
import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.settings.Preference;
import hoshisugi.rukoru.app.services.settings.LocalSettingService;
import hoshisugi.rukoru.framework.annotations.FXController;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import hoshisugi.rukoru.framework.util.DialogUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

@FXController(title = "Home")
public class HomePreferenceController extends BaseController implements PreferenceContent {

	@FXML
	private TextField imageUrl;

	@Inject
	private LocalSettingService service;

	private Map<String, Preference> preferences;

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		loadPreferences();
	}

	private void loadPreferences() {
		ConcurrentUtil.run(() -> {
			final String category = "Home";
			final String key = "imageUrl";
			preferences = service.getPreferencesByCategory(category);
			if (preferences.get(key) == null) {
				preferences.put(key, new Preference(category, key));
			}
			Platform.runLater(() -> imageUrl.textProperty().bindBidirectional(preferences.get(key).valueProperty()));
		});
	}

	@FXML
	private void onApplyButtonClick(final ActionEvent event) {
		apply();
	}

	@Override
	public void apply() {
		if (!Strings.isNullOrEmpty(imageUrl.getText())) {
			ConcurrentUtil.run(() -> {
				service.savePreferences(preferences.values());
				Platform.runLater(() -> DialogUtil.showInfoDialog("完了", "設定を保存しました。"));
			});
		}
	}
}
