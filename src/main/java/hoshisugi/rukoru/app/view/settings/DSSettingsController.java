package hoshisugi.rukoru.app.view.settings;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.enums.DSSettingOperation;
import hoshisugi.rukoru.app.enums.ExecutionType;
import hoshisugi.rukoru.app.enums.StudioMode;
import hoshisugi.rukoru.app.models.ds.DSSetting;
import hoshisugi.rukoru.app.services.settings.LocalSettingService;
import hoshisugi.rukoru.app.view.ds.DSContentController;
import hoshisugi.rukoru.framework.annotations.FXController;
import hoshisugi.rukoru.framework.util.AssetUtil;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import hoshisugi.rukoru.framework.util.DialogUtil;
import hoshisugi.rukoru.framework.util.FXUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;

@FXController(title = "DSSetting")
public class DSSettingsController extends PreferenceControllerBase {

	@Inject
	private LocalSettingService service;

	@Inject
	private DSContentController controller;

	@FXML
	private Button createButton;

	@FXML
	private Button deleteButton;

	@FXML
	private TableView<DSSetting> tableView;

	@FXML
	private TableColumn<DSSetting, String> nameColumn;

	@FXML
	private TableColumn<DSSetting, String> executionPathColumn;

	@FXML
	private TableColumn<DSSetting, ExecutionType> executionTypeColumn;

	@FXML
	private TableColumn<DSSetting, StudioMode> studioModeColumn;

	private final ObservableList<DSSetting> dssettings = FXCollections.observableArrayList();

	private final FilteredList<DSSetting> items = new FilteredList<>(dssettings,
			dssetting -> dssetting.getState() != "Delete");

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		createButton.setGraphic(new ImageView(AssetUtil.getImage("24x24/add.png")));
		deleteButton.setGraphic(new ImageView(AssetUtil.getImage("24x24/delete.png")));
		deleteButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
		nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		executionTypeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(ExecutionType.values()));
		studioModeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(StudioMode.values()));
		tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		tableView.setItems(items);
		loadSettings();
	}

	private void loadSettings() {
		ConcurrentUtil.run(() -> {
			dssettings.clear();
			dssettings.addAll(service.loadDSSettings());
		});
	}

	@FXML
	private void onEntryButtonClick(final ActionEvent event) {
		final EntryDSController controller = FXUtil.popup(EntryDSController.class, FXUtil.getStage(event));
		controller.setOnOkButtonClick(setting -> dssettings.add(setting));
	}

	@FXML
	private void onDeleteButtonClick(final ActionEvent event) {
		final ObservableList<DSSetting> selectedItems = tableView.getSelectionModel().getSelectedItems();
		final Optional<ButtonType> result = DialogUtil.showConfirmDialog("登録解除",
				selectedItems.size() > 1 ? String.format("選択された[%d] 個の登録を解除します。よろしいですか？", selectedItems.size())
						: String.format("[%s] を解除します。よろしいですか？", selectedItems.get(0).getName()));
		if (!result.map(type -> type == ButtonType.OK).orElse(false)) {
			return;
		}
		selectedItems.forEach(s -> s.setState(DSSettingOperation.Delete));
		dssettings.replaceAll(UnaryOperator.identity());
	}

	@Override
	public void apply() {
		final Predicate<DSSetting> p = t -> t.getState() == "Delete";
		ConcurrentUtil.run(() -> {
			service.saveDSSettings(dssettings);
			dssettings.removeAll(dssettings.filtered(p));
			dssettings.forEach(s -> s.setState(DSSettingOperation.Update));
			Platform.runLater(() -> controller.refresh(dssettings));
		});
	}
}
