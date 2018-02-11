package hoshisugi.rukoru.app.view.redmine;

import static com.google.common.base.Strings.nullToEmpty;
import static hoshisugi.rukoru.app.enums.Preferences.RedmineDefaultProject;
import static hoshisugi.rukoru.app.enums.Preferences.RedmineDefaultVersion;
import static hoshisugi.rukoru.app.enums.Preferences.RedmineLoginId;
import static hoshisugi.rukoru.app.enums.Preferences.RedminePassword;
import static hoshisugi.rukoru.framework.util.AssetUtil.loadJS;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
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
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.controls.PropertyListCell;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import hoshisugi.rukoru.framework.util.FXUtil;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class TaskboardController extends BaseController {

	@FXML
	private ComboBox<Project> project;

	@FXML
	private ComboBox<Version> version;

	@FXML
	private VBox content;

	@Inject
	private LocalSettingService settingService;

	@Inject
	private RedmineService redmineService;

	private final Map<String, String> preferences = new HashMap<>();

	private WebView webView;

	private WebEngine engine;

	private int currentColWidth;

	private boolean loggedIn;

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		project.setCellFactory(PropertyListCell.forListView(Project::getName));
		project.setButtonCell(project.getCellFactory().call(null));
		project.getSelectionModel().selectedItemProperty().addListener(this::onProjectSelected);
		version.setCellFactory(PropertyListCell.forListView(Version::getName));
		version.setButtonCell(version.getCellFactory().call(null));
		version.getSelectionModel().selectedItemProperty().addListener(this::onVersionSelected);
		ConcurrentUtil.run(() -> {
			final Projects projects = redmineService.listProjects(new ProjectsRequest());
			project.getItems().setAll(projects.getProjects());
			selectProject();
		});
		Platform.runLater(() -> {
			createWebView();
			load();
		});
	}

	private WebView createWebView() {
		webView = new WebView();
		engine = webView.getEngine();
		VBox.setVgrow(webView, Priority.ALWAYS);
		final Worker<Void> worker = engine.getLoadWorker();
		worker.stateProperty().addListener(this::onLoaded);
		webView.widthProperty().addListener(this::onWidthChanged);
		engine.setCreatePopupHandler(p -> {
			final Stage owner = FXUtil.getStage(webView);
			final VBox parent = new VBox();
			final WebView wv = new WebView();
			parent.getChildren().add(wv);
			VBox.setVgrow(wv, Priority.ALWAYS);
			final Scene scene = new Scene(parent);
			scene.getStylesheets().addAll(owner.getScene().getStylesheets());
			final Stage stage = new Stage();
			stage.initStyle(StageStyle.UTILITY);
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(owner);
			stage.setScene(scene);
			stage.show();
			return wv.getEngine();
		});
		return webView;
	}

	private void onWidthChanged(final ObservableValue<? extends Number> observable, final Number oldValue,
			final Number newValue) {
		int colWidth = (int) (webView.getWidth() - 275) / 315;
		if (colWidth == 0) {
			colWidth = 1;
		}
		if (colWidth != currentColWidth) {
			currentColWidth = colWidth;
			engine.executeScript(loadJS("update_col_widths.js", colWidth));
		}
	}

	private void load() {
		loadPreferences();
		final String version = preferences.get(RedmineDefaultVersion.key());
		if (!preferences.isEmpty() && !Strings.isNullOrEmpty(version)) {
			final String taskboardUrl = String.format("http://redmine.dataspidercloud.tokyo/rb/taskboards/%s", version);
			final Escaper escaper = UrlEscapers.urlFragmentEscaper();
			final String backUrl = escaper.escape(taskboardUrl);
			engine.load("http://redmine.dataspidercloud.tokyo/login?back_url=" + backUrl);
			showLoading();
		} else {
			showContent(new Label("Redmine のログイン情報を設定してください。\n[メニュー] - [Settings] - [Preferences] - [Redmine]"));
		}
	}

	private void showLoading() {
		final StackPane stackPane = new StackPane();
		VBox.setVgrow(stackPane, Priority.ALWAYS);
		final ProgressIndicator progress = new ProgressIndicator();
		progress.setMaxHeight(50.0);
		stackPane.getChildren().add(progress);
		showContent(stackPane);
	}

	private void onLoaded(final ObservableValue<? extends State> observable, final State oldValue,
			final State newValue) {
		if (newValue == Worker.State.SUCCEEDED) {
			final String location = engine.getLocation();
			if (location.contains("/login?back_url=")) {
				onLoginPageLoaded();
				return;
			}
			onTaskboardPageLoaded();
			if (webView.getParent() == null) {
				showContent(webView);
			}
		}
	}

	private void onLoginPageLoaded() {
		engine.executeScript(
				loadJS("login.js", preferences.get(RedmineLoginId.key()), preferences.get(RedminePassword.key())));
		loggedIn = true;
	}

	private void onTaskboardPageLoaded() {
		engine.executeScript(loadJS("hide_header.js"));
	}

	private void loadPreferences() {
		try {
			final Map<String, Preference> preferences = settingService
					.getPreferencesByCategory(RedmineLoginId.category());
			if (!preferences.isEmpty()) {
				this.preferences.putAll(preferences.values().stream()
						.collect(Collectors.toMap(Preference::getKey, Preference::getValue)));
				selectProject();
				selectVersion();
			}
		} catch (final Exception e) {
		}
	}

	private void showContent(final Node node) {
		final ObservableList<Node> children = content.getChildren();
		children.clear();
		children.add(node);
	}

	private void onProjectSelected(final ObservableValue<? extends Project> observable, final Project oldValue,
			final Project newValue) {
		if (newValue != null) {
			ConcurrentUtil.run(() -> {
				final Versions versions = redmineService.listVersions(new VersionsRequest(newValue.getId()));
				Platform.runLater(() -> {
					version.getSelectionModel().clearSelection();
					version.getItems().setAll(versions.getVersions());
					selectVersion();
				});
			});
		}
	}

	private void onVersionSelected(final ObservableValue<? extends Version> observable, final Version oldValue,
			final Version newValue) {
		if (newValue != null && loggedIn) {
			engine.load(newValue.getUrl());
			showLoading();
		}
	}

	private void selectProject() {
		if (!project.getItems().isEmpty() && preferences.containsKey(RedmineDefaultProject.key())) {
			final Integer projectId = Ints.tryParse(nullToEmpty(preferences.get(RedmineDefaultProject.key())));
			final Optional<Project> optional = project.getItems().stream().filter(p -> p.getId().equals(projectId))
					.findFirst();
			optional.ifPresent(p -> {
				Platform.runLater(() -> project.getSelectionModel().select(p));
			});
		}
	}

	private void selectVersion() {
		if (!version.getItems().isEmpty() && preferences.containsKey(RedmineDefaultVersion.key())) {
			final Integer versionId = Ints.tryParse(nullToEmpty(preferences.get(RedmineDefaultVersion.key())));
			final Optional<Version> optional = version.getItems().stream().filter(v -> v.getId().equals(versionId))
					.findFirst();
			optional.ifPresent(v -> {
				Platform.runLater(() -> version.getSelectionModel().select(v));
			});
		}
	}
}
