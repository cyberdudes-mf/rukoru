package hoshisugi.rukoru.app.view.settings;

import static java.lang.Boolean.TRUE;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.enums.RukoruModule;
import hoshisugi.rukoru.app.models.settings.Preference;
import hoshisugi.rukoru.app.services.settings.LocalSettingService;
import hoshisugi.rukoru.app.view.ContentController;
import hoshisugi.rukoru.app.view.ToolBarController;
import hoshisugi.rukoru.framework.annotations.FXController;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.util.AssetUtil;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

@FXController(title = "Module")
public class ModuleActivationController extends BaseController implements PreferenceContent {

	@FXML
	private VBox vbox;

	@Inject
	private LocalSettingService settingService;

	@Inject
	private ContentController contentController;

	@Inject
	private ToolBarController toorBarController;

	private final Map<String, Preference> preferences = new HashMap<>();

	private final Map<RukoruModule, CheckBox> checkBoxes = new HashMap<>();

	@Override
	public void initialize(final URL arg0, final ResourceBundle arg1) {
		for (final RukoruModule rukoruModule : RukoruModule.values()) {
			final CheckBox checkBox = new CheckBox(rukoruModule.getDiplayName());
			checkBox.setFont(new Font(15));
			checkBox.setGraphic(new ImageView(AssetUtil.getImage(rukoruModule.getIconPath())));
			vbox.getChildren().add(checkBox);
			checkBoxes.put(rukoruModule, checkBox);
		}
		loadPreferences();
	}

	private void loadPreferences() {
		ConcurrentUtil.run(() -> {
			preferences.putAll(settingService.getPreferencesByCategory("Module"));
			Platform.runLater(() -> {
				for (final RukoruModule rukoruModule : RukoruModule.values()) {
					final String key = rukoruModule.toString();
					if (!preferences.containsKey(key)) {
						preferences.put(key, new Preference("Module", key, TRUE.toString()));
					}
					final CheckBox checkBox = checkBoxes.get(rukoruModule);
					final Preference preference = preferences.get(key);
					checkBox.setSelected(Boolean.parseBoolean(preference.getValue()));
					preference.valueProperty().bind(Bindings.createStringBinding(() -> {
						return Boolean.toString(checkBox.isSelected());
					}, checkBox.selectedProperty()));
				}
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
			settingService.savePreferences(preferences.values());
			Platform.runLater(() -> {
				contentController.loadContents(preferences);
				toorBarController.refresh(preferences);
			});
		});
	}

}
