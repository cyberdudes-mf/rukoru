package hoshisugi.rukoru.app.view.settings;

import static hoshisugi.rukoru.app.models.scrum.ToolButton.Operation.Edit;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.scrum.ToolButton;
import hoshisugi.rukoru.app.models.scrum.ToolButton.Operation;
import hoshisugi.rukoru.app.services.settings.LocalSettingService;
import hoshisugi.rukoru.app.view.scrum.ScrumController;
import hoshisugi.rukoru.framework.annotations.FXController;
import hoshisugi.rukoru.framework.converter.ColorStringConverter;
import hoshisugi.rukoru.framework.converter.URLStringConverter;
import hoshisugi.rukoru.framework.util.AssetUtil;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

@FXController(title = "Scrum")
public class ScrumSettingController extends PreferenceControllerBase {

	@FXML
	private Button addButton;

	@FXML
	private Button deleteButton;

	@FXML
	private Button upButton;

	@FXML
	private Button downButton;

	@FXML
	private TableView<ToolButton> tableView;

	@FXML
	private TableColumn<ToolButton, String> labelColumn;

	@FXML
	private TableColumn<ToolButton, Color> colorColumn;

	@FXML
	private TableColumn<ToolButton, URL> urlColumn;

	@Inject
	private ScrumController controller;

	@Inject
	private LocalSettingService service;

	private final ObservableList<ToolButton> tools = FXCollections.observableArrayList();

	private final FilteredList<ToolButton> items = new FilteredList<>(tools, t -> t.getOperation() != Operation.Delete);

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		final TableViewSelectionModel<ToolButton> selectionModel = tableView.getSelectionModel();
		addButton.setGraphic(new ImageView(AssetUtil.getImage("24x24/add.png")));
		deleteButton.setGraphic(new ImageView(AssetUtil.getImage("24x24/delete.png")));
		deleteButton.disableProperty().bind(selectionModel.selectedItemProperty().isNull());
		upButton.setGraphic(new ImageView(AssetUtil.getImage("24x24/up.png")));
		upButton.disableProperty().bind(selectionModel.selectedItemProperty().isNull());
		downButton.setGraphic(new ImageView(AssetUtil.getImage("24x24/down.png")));
		downButton.disableProperty().bind(selectionModel.selectedItemProperty().isNull());
		labelColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		colorColumn.setCellFactory(TextFieldTableCell.forTableColumn(new ColorStringConverter()));
		urlColumn.setCellFactory(TextFieldTableCell.forTableColumn(new URLStringConverter()));
		tableView.setItems(items);
		loadToolButtons();
	}

	private void loadToolButtons() {
		ConcurrentUtil.run(() -> {
			tools.clear();
			tools.addAll(service.getToolButtons());
		});
	}

	@Override
	public void apply() {
		ConcurrentUtil.run(() -> {
			tools.stream().filter(t -> Objects.isNull(t.getOperation())).forEach(t -> t.setOperation(Edit));
			service.saveToolButtons(tools);
			final List<ToolButton> toolButtons = service.getToolButtons();
			controller.refreshToolButtons();
			Platform.runLater(() -> {
				tools.clear();
				tools.addAll(toolButtons);
			});
		});
	}

	@FXML
	private void onAddButtonClick(final ActionEvent event) {
		final ToolButton item = new ToolButton();
		item.setOperation(Operation.Add);
		tools.add(item);
	}

	@FXML
	private void onDeleteButtonClick(final ActionEvent event) {
		final ToolButton item = tableView.getSelectionModel().getSelectedItem();
		if (item.getOperation() == Operation.Add) {
			tools.remove(item);
			return;
		}
		item.setOperation(Operation.Delete);
	}

	@FXML
	private void onUpButtonClick(final ActionEvent event) {
	}

	@FXML
	private void onDownButtonClick(final ActionEvent event) {
	}

}
