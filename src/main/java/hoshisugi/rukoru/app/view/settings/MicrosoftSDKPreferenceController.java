package hoshisugi.rukoru.app.view.settings;

import static hoshisugi.rukoru.app.enums.Preferences.MicrosoftSDKPath;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.settings.Preference;
import hoshisugi.rukoru.app.services.settings.LocalSettingService;
import hoshisugi.rukoru.framework.annotations.FXController;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

@FXController(title = "Microsoft SDK")
public class MicrosoftSDKPreferenceController extends BaseController implements PreferenceContent {

	@FXML
	private TextField path;

	@Inject
	private LocalSettingService service;

	private final Map<String, Preference> preferences = new HashMap<>();

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		loadPreferences();
	}

	private void loadPreferences() {
		ConcurrentUtil.run(() -> {
			final String category = "MicrosoftSDK";
			final String key = "path";
			final Map<String, Preference> sdkPreferences = service.getPreferencesByCategory(category);
			preferences.putAll(sdkPreferences);
			if (preferences.get(key) == null) {
				final Preference preference = new Preference(category, key);
				preference.setValue(MicrosoftSDKPath.getDefaultValue());
				preferences.put(key, preference);
			}
			Platform.runLater(() -> path.textProperty().bindBidirectional(preferences.get(key).valueProperty()));
		});
	}

	@FXML
	private void onApplyButtonClick(final ActionEvent event) {
		apply();
	}

	@Override
	public void apply() {
		ConcurrentUtil.run(() -> {
			service.savePreferences(preferences.values());
		});
	}
}
