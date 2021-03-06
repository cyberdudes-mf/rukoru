package hoshisugi.rukoru.app.view.settings;

import static hoshisugi.rukoru.app.enums.Preferences.ImageUrl;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.settings.Preference;
import hoshisugi.rukoru.app.services.settings.LocalSettingService;
import hoshisugi.rukoru.app.view.ContentController;
import hoshisugi.rukoru.framework.annotations.FXController;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

@FXController(title = "Home")
public class HomePreferenceController extends PreferenceControllerBase {

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
			final String key = ImageUrl.key();
			preferences.putAll(service.getPreferencesByCategory("Home"));
			if (preferences.get(key) == null) {
				preferences.put(key, new Preference(ImageUrl));
			}
			Platform.runLater(() -> imageUrl.textProperty().bindBidirectional(preferences.get(key).valueProperty()));
		});
	}

	@Override
	public void apply() {
		ConcurrentUtil.run(() -> {
			service.savePreferences(preferences.values());
			contentController.refreshTopImage();
		});
	}
}