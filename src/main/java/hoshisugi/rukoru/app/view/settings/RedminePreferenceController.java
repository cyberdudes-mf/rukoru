package hoshisugi.rukoru.app.view.settings;

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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

@FXController(title = "Redmine")
public class RedminePreferenceController extends BaseController implements PreferenceContent {

	@FXML
	private TextField loginId;

	@FXML
	private PasswordField password;

	@Inject
	private LocalSettingService service;

	private final Map<String, Preference> preferences = new HashMap<>();

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		loadPreferences();
	}

	private void loadPreferences() {
		ConcurrentUtil.run(() -> {
			final String category = "Redmine";
			final String loginIdKey = "loginId";
			final String passwordKey = "password";
			preferences.putAll(service.getPreferencesByCategory(category));
			if (preferences.get(loginIdKey) == null) {
				preferences.put(loginIdKey, new Preference(category, loginIdKey));
			}
			if (preferences.get(passwordKey) == null) {
				preferences.put(passwordKey, new Preference(category, passwordKey));
			}
			Platform.runLater(() -> {
				loginId.textProperty().bindBidirectional(preferences.get(loginIdKey).valueProperty());
				password.textProperty().bindBidirectional(preferences.get(passwordKey).valueProperty());
			});
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
