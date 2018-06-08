package hoshisugi.rukoru.app.view.ds;

import static hoshisugi.rukoru.app.enums.Preferences.DSSettingShowConsole;
import static hoshisugi.rukoru.framework.util.AssetUtil.getImage;
import static javafx.beans.binding.Bindings.when;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.ds.DSEntry;
import hoshisugi.rukoru.app.models.ds.DSLogWriter;
import hoshisugi.rukoru.app.models.ds.DSManager;
import hoshisugi.rukoru.app.models.ds.DSSetting;
import hoshisugi.rukoru.app.models.settings.Preference;
import hoshisugi.rukoru.app.services.ds.DSService;
import hoshisugi.rukoru.app.services.settings.LocalSettingService;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.cli.CLI;
import hoshisugi.rukoru.framework.util.AssetUtil;
import hoshisugi.rukoru.framework.util.BrowserUtil;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import hoshisugi.rukoru.framework.util.FXUtil;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class DSEntryController extends BaseController implements DSEntry {

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

	@FXML
	private TextField port;

	@FXML
	private Button changePortButton;

	@FXML
	private Button settingPropertiesButton;

	@FXML
	private Button showHelpButton;

	@Inject
	private DSService dsService;

	@Inject
	private LocalSettingService settingService;

	private final Button showConsoleButton = new Button();

	private DSSetting dsSetting;

	public Integer getDSSettingId() {
		return dsSetting.getId();
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
		settingPropertiesButton.setGraphic(new ImageView(getImage("32x32/gear.png")));
		showHelpButton.setGraphic(new ImageView(getImage("32x32/help.png")));
		showHelpButton.disableProperty().bind(controlServerButton.selectedProperty().not());
		showConsoleButton.setGraphic(new ImageView(getImage("32x32/console.png")));
		showConsoleButton.setOnAction(this::onStartConsoleButtonClick);
		controlServerButton.selectedProperty().addListener(this::onSelectedPropertyCahnged);
		controlStudioButton.selectedProperty().addListener(this::onSelectedPropertyCahnged);

		final BooleanBinding selected = controlServerButton.selectedProperty()
				.isNotEqualTo(controlStudioButton.selectedProperty());
		final BooleanBinding disabled = controlServerButton.disableProperty().or(controlStudioButton.disableProperty());
		controlAllButton.disableProperty().bind(selected.or(disabled));
	}

	@FXML
	private void onOpenHomeButtonClick(final ActionEvent event) {
		CLI.command("explorer").options(dsSetting.getExecutionPath()).execute();
	}

	@FXML
	private void onControlServerButtonClick(final ActionEvent event) {
		final DSManager manager = DSManager.getManager(this);
		if (controlServerButton.isSelected()) {
			manager.startServer();
		} else {
			manager.stopServer();
		}
	}

	@FXML
	private void onControlStudioButtonClick(final ActionEvent event) {
		final DSManager manager = DSManager.getManager(this);
		if (controlStudioButton.isSelected()) {
			manager.startStudio();
		} else {
			manager.stopStudio();
		}
	}

	@FXML
	private void onControlAllButtonClick(final ActionEvent event) {
		final DSManager manager = DSManager.getManager(this);
		if (controlAllButton.isSelected()) {
			manager.startBoth();
		} else {
			manager.stopBoth();
		}
	}

	@FXML
	private void onChangePortButtonClick(final ActionEvent event) {
		ConcurrentUtil.run(() -> {
			dsService.changePort(dsSetting, port.getText());
			Platform.runLater(() -> FXUtil.showTooltip("ポートを変更しました。", event));
		});
	}

	@FXML
	private void onSettingPropertiesButtonClick(final ActionEvent event) {
		final DSPropertiesController controller = FXUtil.popup(DSPropertiesController.class, FXUtil.getStage(event));
		controller.setDSSetting(dsSetting);
	}

	@FXML
	private void onShowHelpButtonClick(final ActionEvent event) {
		BrowserUtil.browse(dsSetting.getHelpUrl());
	}

	private void onStartConsoleButtonClick(final ActionEvent event) {
		dsService.startConsole(dsSetting);
	}

	public void loadSetting(final DSSetting dsSetting) {
		Platform.runLater(() -> {
			name.setText(dsSetting.getName());
			port.setText(dsSetting.getPort());
			controlServerButton.setDisable(!dsSetting.isServerInstalled());
			controlStudioButton.setDisable(!dsSetting.isStudioInstalled());
			controlServerButton.setSelected(dsService.isServerRunning(dsSetting));
			controlStudioButton.setSelected(dsService.isStudioRunning(dsSetting));
			port.setDisable(!dsSetting.isServerInstalled());
			changePortButton.setDisable(!dsSetting.isStudioInstalled());
			settingPropertiesButton.setDisable(!dsSetting.isServerInstalled());
		});
		showConsoleButtonIfEnabled();
		this.dsSetting = dsSetting;
	}

	private void showConsoleButtonIfEnabled() {
		ConcurrentUtil.run(() -> {
			final Boolean showConsole = settingService.findPreference(DSSettingShowConsole).map(Preference::getValue)
					.map(Boolean::parseBoolean).orElse(false);
			final HBox parent = (HBox) titledPane.getGraphic();
			final ObservableList<Node> children = parent.getChildren();
			Platform.runLater(() -> {
				if (showConsole) {
					if (!children.contains(showConsoleButton)) {
						children.add(children.indexOf(port.getParent()), showConsoleButton);
					}
				} else {
					if (children.contains(showConsoleButton)) {
						parent.getChildren().remove(showConsoleButton);
					}
				}
			});
		});
	}

	private void onSelectedPropertyCahnged(final ObservableValue<? extends Boolean> observable, final Boolean oldValue,
			final Boolean newValue) {
		controlAllButton.setSelected(controlServerButton.isSelected() || controlStudioButton.isSelected());
	}

	@Override
	public DSSetting getDsSetting() {
		return dsSetting;
	}

	@Override
	public DSLogWriter getServerLogWriter() {
		if (accordion.getExpandedPane() == null) {
			accordion.setExpandedPane(titledPane);
		}
		final SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
		if (selectionModel.getSelectedItem() != serverConsole) {
			selectionModel.select(serverConsole);
		}
		return new DSLogWriter(serverLogText);
	}

	@Override
	public DSLogWriter getStudioLogWriter() {
		if (accordion.getExpandedPane() == null) {
			accordion.setExpandedPane(titledPane);
		}
		final SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
		if (selectionModel.getSelectedItem() != studioConsole) {
			selectionModel.select(studioConsole);
		}
		return new DSLogWriter(studioLogText);
	}

	@Override
	public void setServerButtonDisable(final boolean disable) {
		controlServerButton.setDisable(disable);
	}

	@Override
	public void setStudioButtonDisable(final boolean disable) {
		controlStudioButton.setDisable(disable);
	}

	@Override
	public void setServerButtonSelected(final boolean disable) {
		controlServerButton.setSelected(disable);
	}

	@Override
	public void setStudioButtonSelected(final boolean disable) {
		controlStudioButton.setSelected(disable);
	}

	@Override
	public boolean isServerButtonSelected() {
		return controlServerButton.isSelected();
	}

	@Override
	public boolean isStudioButtonSelected() {
		return controlStudioButton.isSelected();
	}

}
