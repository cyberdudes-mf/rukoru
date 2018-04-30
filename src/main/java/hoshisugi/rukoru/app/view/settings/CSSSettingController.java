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
	private ComboBox<String> cssThemes;

	@Inject
	private LocalSettingService service;

	private String defaultCSS;

	private final Map<String, Preference> preferences = new HashMap<>();

	@Override
	public void initialize(final URL arg0, final ResourceBundle arg1) {
		cssThemes.getItems().addAll(CSSThemes.toAllay());
		loadPreferences();
	}

	private void loadPreferences() {
		ConcurrentUtil.run(() -> {
			preferences.putAll(service.getPreferencesByCategory("CSS"));
			if (preferences.size() == 0) {
				preferences.put(CSSTheme.key(), new Preference(CSSTheme));
				cssThemes.getSelectionModel().select("Modena");
			}
			Platform.runLater(() -> {
				cssThemes.getSelectionModel().select(preferences.get(CSSTheme.key()).getValue());
				preferences.get(CSSTheme.key()).valueProperty()
						.bind(cssThemes.getSelectionModel().selectedItemProperty());
				cssThemes.getSelectionModel().selectedItemProperty()
						.addListener((obserbableValue, oldValue, newValue) -> {
							ConcurrentUtil.run(() -> service.changeStyleSheet(newValue));
						});
				defaultCSS = cssThemes.getSelectionModel().getSelectedItem();
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
			defaultCSS = cssThemes.getSelectionModel().getSelectedItem();
			service.savePreferences(preferences.values());
		});
	}

	@Override
	public void cancel() {
		ConcurrentUtil.run(() -> {
			if (defaultCSS != cssThemes.getSelectionModel().getSelectedItem()) {
				service.setStyleSheet();
			}
		});
	}
}
