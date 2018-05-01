package hoshisugi.rukoru.app.view.settings;

import static hoshisugi.rukoru.app.enums.Preferences.CSSTheme;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.enums.CSSThemes;
import hoshisugi.rukoru.app.models.settings.Preference;
import hoshisugi.rukoru.app.services.settings.LocalSettingService;
import hoshisugi.rukoru.framework.annotations.FXController;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

@FXController(title = "CSS")
public class CSSSettingController extends BaseController implements PreferenceContent {

	@FXML
	private ComboBox<CSSThemes> cssThemes;

	@Inject
	private LocalSettingService service;

	private final Map<String, Preference> preferences = new HashMap<>();

	@Override
	public void initialize(final URL arg0, final ResourceBundle arg1) {
		cssThemes.getItems().addAll(CSSThemes.values());
		loadPreferences();
	}

	private void loadPreferences() {
		ConcurrentUtil.run(() -> {
			preferences.putAll(service.getPreferencesByCategory("CSS"));
			if (preferences.size() == 0) {
				preferences.put(CSSTheme.key(), new Preference(CSSTheme));
				preferences.get(CSSTheme.key()).setValue("Modena");
			}
			Platform.runLater(() -> {
				cssThemes.getSelectionModel().selectedItemProperty()
						.addListener((observableValue, oldValue, newValue) -> {
							service.changeStyleSheet(newValue);
							preferences.get(CSSTheme.key()).setValue(newValue.toString());
						});
				cssThemes.getSelectionModel().select(CSSThemes.of(preferences.get(CSSTheme.key()).getValue()));
			});
		});
	}

	@FXML
	private void onApplyButtonClick() {
		apply();
	}

	@Override
	public void apply() {
		ConcurrentUtil.run(() -> {
			service.savePreferences(preferences.values());
		});
	}

	@Override
	public void cancel() {
		ConcurrentUtil.run(() -> {
			service.setStyleSheet();
		});
	}
}
