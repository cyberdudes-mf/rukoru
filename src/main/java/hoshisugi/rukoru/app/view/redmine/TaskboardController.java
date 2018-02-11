package hoshisugi.rukoru.app.view.redmine;

import static hoshisugi.rukoru.app.enums.Preferences.RedmineLoginId;
import static hoshisugi.rukoru.app.enums.Preferences.RedminePassword;
import static hoshisugi.rukoru.framework.util.AssetUtil.loadJS;

import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.settings.Preference;
import hoshisugi.rukoru.app.services.settings.LocalSettingService;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.util.AssetUtil;
import hoshisugi.rukoru.framework.util.DialogUtil;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class TaskboardController extends BaseController {

	@FXML
	private VBox layoutRoot;

	@FXML
	private Button backButton;

	@FXML
	private Button prevButton;

	@FXML
	private Button refreshButton;

	@Inject
	private LocalSettingService service;

	private WebView webView;

	private WebEngine engine;

	private int currentColWidth;

	private Map<String, String> loginInfo;

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		backButton.setGraphic(new ImageView(AssetUtil.getImage("16x16/back.png")));
		prevButton.setGraphic(new ImageView(AssetUtil.getImage("16x16/next.png")));
		refreshButton.setGraphic(new ImageView(AssetUtil.getImage("16x16/refresh.png")));
		Platform.runLater(() -> {
			createWebView();
			loadLoginPage();
		});
	}

	private WebView createWebView() {
		webView = new WebView();
		engine = webView.getEngine();
		VBox.setVgrow(webView, Priority.ALWAYS);
		final Worker<Void> worker = engine.getLoadWorker();
		worker.stateProperty().addListener(this::onLoaded);
		webView.widthProperty().addListener(this::onWidthChanged);
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

	private void loadLoginPage() {
		loginInfo = getLoginInfo();
		if (!loginInfo.isEmpty()) {
			final String taskboardUrl = "http://redmine.dataspidercloud.tokyo/rb/taskboards/34";
			final Escaper escaper = UrlEscapers.urlFragmentEscaper();
			final String backUrl = escaper.escape(taskboardUrl);
			engine.load("http://redmine.dataspidercloud.tokyo/login?back_url=" + backUrl);
		} else {
			DialogUtil.showWarningDialog("Redmine のログイン情報を設定してください。\n[メニュー] - [Settings] - [Preferences] - [Redmine]");
		}
	}

	@FXML
	private void onBackButtonClicked(final ActionEvent event) {

	}

	@FXML
	private void onPrevButtonClicked(final ActionEvent event) {

	}

	@FXML
	private void onRefreshButtonClicked(final ActionEvent event) {

	}

	private void onLoaded(final ObservableValue<? extends State> observable, final State oldValue,
			final State newValue) {
		if (newValue == Worker.State.SUCCEEDED) {
			final String location = engine.getLocation();
			if (location.contains("/login")) {
				onLoginPageLoaded();
			} else if (location.contains("/rb/taskboards/")) {
				onTaskboardPageLoaded();
			}
		}
	}

	private void onLoginPageLoaded() {
		engine.executeScript(
				loadJS("login.js", loginInfo.get(RedmineLoginId.getKey()), loginInfo.get(RedminePassword.getKey())));
	}

	private void onTaskboardPageLoaded() {
		engine.executeScript(loadJS("hide_header.js"));
		showTaskboard();
	}

	private Map<String, String> getLoginInfo() {
		try {
			final Map<String, Preference> preferences = service.getPreferencesByCategory(RedmineLoginId.getCategory());
			if (preferences.size() != 2) {
				return Collections.emptyMap();
			}
			return preferences.values().stream().collect(Collectors.toMap(Preference::getKey, Preference::getValue));
		} catch (final Exception e) {
			return Collections.emptyMap();
		}
	}

	private void showTaskboard() {
		Platform.runLater(() -> {
			final ObservableList<Node> children = layoutRoot.getChildren();
			children.remove(children.size() - 1);
			children.add(webView);
		});

	}
}
