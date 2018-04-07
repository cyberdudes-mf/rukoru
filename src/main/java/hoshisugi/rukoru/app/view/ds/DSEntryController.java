package hoshisugi.rukoru.app.view.ds;

import static hoshisugi.rukoru.framework.util.AssetUtil.getImage;
import static javafx.beans.binding.Bindings.when;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.ds.DSLogWriter;
import hoshisugi.rukoru.app.models.ds.DSSetting;
import hoshisugi.rukoru.app.services.ds.DSService;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.cli.CLI;
import hoshisugi.rukoru.framework.cli.CLIState;
import hoshisugi.rukoru.framework.util.AssetUtil;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import hoshisugi.rukoru.framework.util.FXUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.stage.WindowEvent;

public class DSEntryController extends BaseController {

	@FXML
	private Accordion accordion;

	@FXML
	private TitledPane titledPane;

	@FXML
	private Button openHomeButton;

	@FXML
	private Label name;

	@FXML
	private ToggleButton controlServerButton;

	@FXML
	private ToggleButton controlStudioButton;

	@FXML
	private ToggleButton controlAllButton;

	@FXML
	private TabPane tabPane;

	@FXML
	private Tab serverConsole;

	@FXML
	private Tab studioConsole;

	@FXML
	private TextArea serverLogText;

	@FXML
	private TextArea studioLogText;

	@Inject
	private DSService service;

	private DSSetting dsSetting;

	private final EventHandler<WindowEvent> stopOnExit = e -> this.stopOnExit();

	public Integer getDSSettingId() {
		return dsSetting.getId();
	}

	public void setServerDisable(final boolean value) {
		controlServerButton.setDisable(value);
	}

	public void setStudioDisable(final boolean value) {
		controlStudioButton.setDisable(value);
	}

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		openHomeButton.setGraphic(new ImageView(AssetUtil.getImage("32x32/folder.png")));
		controlServerButton.graphicProperty().bind(when(controlServerButton.selectedProperty().not())
				.then(new ImageView(getImage("32x32/run.png"))).otherwise(new ImageView(getImage("32x32/stop.png"))));
		controlStudioButton.graphicProperty().bind(when(controlStudioButton.selectedProperty().not())
				.then(new ImageView(getImage("32x32/run.png"))).otherwise(new ImageView(getImage("32x32/stop.png"))));
		controlAllButton.graphicProperty().bind(when(controlAllButton.selectedProperty().not())
				.then(new ImageView(getImage("32x32/run.png"))).otherwise(new ImageView(getImage("32x32/stop.png"))));
	}

	@FXML
	private void onOpenHomeButtonClick(final ActionEvent event) {
		CLI.command("explorer").options(dsSetting.getExecutionPath()).execute();
	}

	@FXML
	private void onControlServerButtonClick(final ActionEvent event) {
		if (accordion.getExpandedPane() == null) {
			accordion.setExpandedPane(titledPane);
		}
		final SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
		if (selectionModel.getSelectedItem() != serverConsole) {
			selectionModel.select(serverConsole);
		}
		controlServerButton.setDisable(true);
		if (controlServerButton.isSelected()) {
			serverLogText.clear();
			ConcurrentUtil.run(() -> {
				final String executionType = dsSetting.getExecutionType();
				switch (executionType) {
				case "EXE":
					service.startServerWithExe(dsSetting, new DSLogWriter(serverLogText), this::onServerStarted);
					break;
				case "SERVICE":
					service.startServerWithService(dsSetting, new DSLogWriter(serverLogText), this::onServerStarted);
					break;
				case "Bat":
					// 未実装
					break;
				default:
					break;
				}
			});
		} else {
			ConcurrentUtil.run(() -> service.stopServerWithExe(dsSetting, this::onServerStopped));
		}
	}

	@FXML
	private void onControlStudioButtonClick(final ActionEvent event) {
		if (accordion.getExpandedPane() == null) {
			accordion.setExpandedPane(titledPane);
		}
		final SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
		if (selectionModel.getSelectedItem() != studioConsole) {
			selectionModel.select(studioConsole);
		}
		controlStudioButton.setDisable(true);
		if (controlStudioButton.isSelected()) {
			studioLogText.clear();
			ConcurrentUtil.run(
					() -> service.startStudioWithExe(dsSetting, new DSLogWriter(studioLogText), this::onStudioStarted));
		} else {
			ConcurrentUtil.run(() -> service.stopStudioWithExe(dsSetting, this::onStudioStopped));
		}
	}

	@FXML
	private void onControlAllButtonClick(final ActionEvent event) {
		System.out.println("onControlAllButtonClick");
	}

	public void loadSetting(final DSSetting dsSetting) {
		name.setText(dsSetting.getName());
		this.dsSetting = dsSetting;
	}

	private void onServerStarted(final CLIState cliState) {
		if (controlServerButton.isDisable()) {
			Platform.runLater(() -> controlServerButton.setDisable(false));
		}
		if (cliState.isSuccess()) {
			FXUtil.getPrimaryStage().setOnCloseRequest(stopOnExit);
		}
		if (cliState.isFailure()) {
			Platform.runLater(() -> controlServerButton.setSelected(false));
		}
	}

	private void onServerStopped(final CLIState cliState) {
		if (controlServerButton.isDisable()) {
			Platform.runLater(() -> controlServerButton.setDisable(false));
		}
		if (cliState.isFailure()) {
			Platform.runLater(() -> controlServerButton.setSelected(true));
		}
	}

	private void onStudioStarted(final CLIState cliState) {
		if (controlStudioButton.isDisable()) {
			Platform.runLater(() -> controlStudioButton.setDisable(false));
		}
		if (cliState.isFailure()) {
			Platform.runLater(() -> controlStudioButton.setSelected(false));
		}
	}

	private void onStudioStopped(final CLIState cliState) {
		if (controlStudioButton.isDisable()) {
			Platform.runLater(() -> controlStudioButton.setDisable(false));
		}
		if (cliState.isFailure()) {
			Platform.runLater(() -> controlStudioButton.setSelected(true));
		}
	}

	private void stopOnExit() {
		if (controlServerButton.isSelected()) {
			ConcurrentUtil.run(() -> service.stopServerWithExe(dsSetting, this::onServerStopped));
		}
		if (controlStudioButton.isSelected()) {
			ConcurrentUtil.run(() -> service.stopServerWithExe(dsSetting, this::onServerStopped));
		}
	}
}
