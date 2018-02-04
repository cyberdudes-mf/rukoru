package hoshisugi.rukoru.app.view.settings;

import static hoshisugi.rukoru.app.enums.Preferences.HomeImageUrl;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.settings.Preference;
import hoshisugi.rukoru.app.services.settings.LocalSettingService;
import hoshisugi.rukoru.app.view.ContentController;
import hoshisugi.rukoru.framework.annotations.FXController;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
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

	@Inject
	private ContentController contentController;

	private final Map<String, Preference> preferences = new HashMap<>();

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		loadPreferences();
	}

	private void loadPreferences() {
		ConcurrentUtil.run(() -> {
			final String category = "Home";
			final String key = "imageUrl";
			preferences.putAll(service.getPreferencesByCategory(category));
			if (preferences.get(key) == null) {
				final Preference preference = new Preference(category, key);
				preference.setValue(HomeImageUrl.getDefaultValue());
				preferences.put(key, preference);
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
		ConcurrentUtil.run(() -> {
			service.savePreferences(preferences.values());
			Platform.runLater(() -> contentController.refreshTopImage());
		});
	}
}
