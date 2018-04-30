package hoshisugi.rukoru.app.view.settings;

import static com.google.common.base.Strings.nullToEmpty;
import static hoshisugi.rukoru.app.enums.Preferences.RedmineDefaultProject;
import static hoshisugi.rukoru.app.enums.Preferences.RedmineDefaultVersion;
import static hoshisugi.rukoru.app.enums.Preferences.RedmineLoginId;
import static hoshisugi.rukoru.app.enums.Preferences.RedminePassword;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import com.google.common.primitives.Ints;
import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.redmine.Project;
import hoshisugi.rukoru.app.models.redmine.Projects;
import hoshisugi.rukoru.app.models.redmine.ProjectsRequest;
import hoshisugi.rukoru.app.models.redmine.Version;
import hoshisugi.rukoru.app.models.redmine.Versions;
import hoshisugi.rukoru.app.models.redmine.VersionsRequest;
import hoshisugi.rukoru.app.models.settings.Preference;
import hoshisugi.rukoru.app.services.redmine.RedmineService;
import hoshisugi.rukoru.app.services.settings.LocalSettingService;
import hoshisugi.rukoru.app.view.redmine.TaskboardController;
import hoshisugi.rukoru.framework.annotations.FXController;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.controls.PropertyListCell;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

@FXController(title = "Redmine")
public class RedminePreferenceController extends BaseController implements PreferenceContent {

	@FXML
	private TextField loginId;

	@FXML
	private PasswordField password;

	@FXML
	private ComboBox<Project> defaultProject;

	@FXML
	private ComboBox<Version> defaultVersion;

	@Inject
	private TaskboardController controller;

	@Inject
	private LocalSettingService settingService;

	@Inject
	private RedmineService redmineService;

	private final Map<String, Preference> preferences = new HashMap<>();

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		ConcurrentUtil.run(() -> {
			final Projects projects = redmineService.listProjects(new ProjectsRequest());
			defaultProject.getItems().setAll(projects.getProjects());
			selectDefaultProject();
		});
		loadPreferences();
		defaultProject.setCellFactory(PropertyListCell.forListView(Project::getName));
		defaultProject.setButtonCell(defaultProject.getCellFactory().call(null));
		defaultProject.getSelectionModel().selectedItemProperty().addListener(this::onDefaultProjectSelected);
		defaultVersion.setCellFactory(PropertyListCell.forListView(Version::getName));
		defaultVersion.setButtonCell(defaultVersion.getCellFactory().call(null));
		defaultVersion.getSelectionModel().selectedItemProperty().addListener(this::onDefaultVersionSelected);
	}

	private void loadPreferences() {
		ConcurrentUtil.run(() -> {
			preferences.putAll(settingService.getPreferencesByCategory("Redmine"));
			if (preferences.get(RedmineLoginId.key()) == null) {
				preferences.put(RedmineLoginId.key(), new Preference(RedmineLoginId));
			}
			if (preferences.get(RedminePassword.key()) == null) {
				preferences.put(RedminePassword.key(), new Preference(RedminePassword));
			}
			if (preferences.get(RedmineDefaultProject.key()) == null) {
				preferences.put(RedmineDefaultProject.key(), new Preference(RedmineDefaultProject));
			}
			if (preferences.get(RedmineDefaultVersion.key()) == null) {
				preferences.put(RedmineDefaultVersion.key(), new Preference(RedmineDefaultVersion));
			}
			Platform.runLater(() -> {
				loginId.textProperty().bindBidirectional(preferences.get(RedmineLoginId.key()).valueProperty());
				password.textProperty().bindBidirectional(preferences.get(RedminePassword.key()).valueProperty());
				selectDefaultProject();
				selectDefaultVersion();
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
			controller.load();
		});
	}

	private void onDefaultProjectSelected(final ObservableValue<? extends Project> observable, final Project oldValue,
			final Project newValue) {
		if (newValue != null) {
			preferences.get(RedmineDefaultProject.key()).setValue(newValue.getId().toString());
			ConcurrentUtil.run(() -> {
				final Versions versions = redmineService.listVersions(new VersionsRequest(newValue.getId()));
				Platform.runLater(() -> {
					defaultVersion.getSelectionModel().clearSelection();
					defaultVersion.getItems().setAll(versions.getVersions());
					selectDefaultVersion();
				});
			});
		}
	}

	private void onDefaultVersionSelected(final ObservableValue<? extends Version> observable, final Version oldValue,
			final Version newValue) {
		if (newValue != null) {
			preferences.get(RedmineDefaultVersion.key()).setValue(newValue.getId().toString());
		}
	}

	private void selectDefaultProject() {
		if (!defaultProject.getItems().isEmpty() && preferences.containsKey(RedmineDefaultProject.key())) {

			final Integer projectId = Ints
					.tryParse(nullToEmpty(preferences.get(RedmineDefaultProject.key()).getValue()));
			final Optional<Project> project = defaultProject.getItems().stream()
					.filter(p -> p.getId().equals(projectId)).findFirst();
			project.ifPresent(p -> {
				Platform.runLater(() -> defaultProject.getSelectionModel().select(p));
			});
		}
	}

	private void selectDefaultVersion() {
		if (!defaultVersion.getItems().isEmpty() && preferences.containsKey(RedmineDefaultVersion.key())) {
			final Integer versionId = Ints
					.tryParse(nullToEmpty(preferences.get(RedmineDefaultVersion.key()).getValue()));
			final Optional<Version> version = defaultVersion.getItems().stream()
					.filter(v -> v.getId().equals(versionId)).findFirst();
			version.ifPresent(v -> {
				Platform.runLater(() -> defaultVersion.getSelectionModel().select(v));
			});
		}
	}

	@Override
	public void cancel() {
	}
}
