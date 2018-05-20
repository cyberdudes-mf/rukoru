package hoshisugi.rukoru.app.view.ds;

import static hoshisugi.rukoru.app.enums.DSProperties.Properties;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import hoshisugi.rukoru.app.enums.DSProperties;
import hoshisugi.rukoru.app.models.ds.DSProperty;
import hoshisugi.rukoru.app.models.ds.DSSetting;
import hoshisugi.rukoru.framework.annotations.FXController;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.util.AssetUtil;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import hoshisugi.rukoru.framework.util.FXUtil;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;

@FXController(title = "Properties")
public class DSPropertiesController extends BaseController {

	@FXML
	private TreeView<DSProperties> treeView;

	@FXML
	private TableView<DSProperty> tableView;

	@FXML
	private TableColumn<DSProperty, Boolean> isEnableColumn;

	@FXML
	private TableColumn<DSProperty, String> keyColumn;

	@FXML
	private TableColumn<DSProperty, String> valueColumn;

	@FXML
	private Button addButton;

	@FXML
	private Button deleteButton;

	@FXML
	private Button CloseButton;

	private DSSetting dsSetting;

	public void setDSSetting(final DSSetting dsSetting) {
		this.dsSetting = dsSetting;
	}

	@Override
	public void initialize(final URL arg0, final ResourceBundle arg1) {
		createTree();
		treeView.getSelectionModel().select(0);
		treeView.getSelectionModel().selectedItemProperty().addListener(this::onSelectedItemChanged);
		createTablePane();
	}

	private void createTree() {
		final TreeItem<DSProperties> root = new TreeItem<>(Properties);
		root.setExpanded(true);
		Stream.of(DSProperties.values()).filter(v -> v.getPath() != null).map(p -> new TreeItem<>(p))
				.forEach(t -> root.getChildren().add(t));
		treeView.setRoot(root);
	}

	private void createTablePane() {
		isEnableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(isEnableColumn));
		isEnableColumn.setCellValueFactory(param -> param.getValue().getIsEnableProperty());
		keyColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		addButton.setGraphic(new ImageView(AssetUtil.getImage("24x24/add.png")));
		deleteButton.setGraphic(new ImageView(AssetUtil.getImage("24x24/delete.png")));
	}

	@FXML
	private void onApplyButtonClick(final ActionEvent event) {
		Apply();
	}

	@FXML
	private void onApplyandCloseButtonClick(final ActionEvent event) {
		Apply();
		FXUtil.getStage(event).close();
	}

	@FXML
	private void onCloseButtonClick(final ActionEvent event) {
		FXUtil.getStage(event).close();
	}

	private void loadProperties(final DSProperties properties) throws IOException {
		if (properties.getPath() == null) {
			return;
		}
		try (final BufferedReader br = Files
				.newBufferedReader(Paths.get(dsSetting.getExecutionPath(), properties.getPath()))) {
			final List<DSProperty> list = br.lines().filter(s -> s.matches("(#)?[\\w.]*=[\\w\'#/:,-. ]*"))
					.map(DSProperty::new).collect(Collectors.toList());
			Platform.runLater(() -> {
				tableView.getItems().clear();
				tableView.getItems().addAll(list);
			});
		}
	}

	private void onSelectedItemChanged(final ObservableValue<? extends TreeItem<DSProperties>> observable,
			final TreeItem<DSProperties> oldValue, final TreeItem<DSProperties> newValue) {
		ConcurrentUtil.run(() -> loadProperties(newValue.getValue()));
	}

	private void Apply() {

	}

}
