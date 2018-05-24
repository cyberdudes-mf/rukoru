package hoshisugi.rukoru.app.view.ds;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import hoshisugi.rukoru.app.enums.DSProperties;
import hoshisugi.rukoru.app.models.ds.DSProperty;
import hoshisugi.rukoru.app.models.ds.DSPropertyManager;
import hoshisugi.rukoru.app.models.ds.DSSetting;
import hoshisugi.rukoru.framework.annotations.FXController;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.util.AssetUtil;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import hoshisugi.rukoru.framework.util.DialogUtil;
import hoshisugi.rukoru.framework.util.FXUtil;
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
import javafx.scene.layout.VBox;

@FXController(title = "Properties")
public class DSPropertiesController extends BaseController {

	@FXML
	private TreeView<String> treeView;

	@FXML
	private VBox layoutRoot;

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

	private DSPropertyManager manager;

	public void setDSSetting(final DSSetting dsSetting) {
		this.dsSetting = dsSetting;
	}

	@Override
	public void initialize(final URL arg0, final ResourceBundle arg1) {
		createTree();
		createTablePane();
		addButton.setGraphic(new ImageView(AssetUtil.getImage("24x24/add.png")));
		deleteButton.setGraphic(new ImageView(AssetUtil.getImage("24x24/delete.png")));
		deleteButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
	}

	private void createTree() {
		final TreeItem<String> root = new TreeItem<>();
		Stream.of(DSProperties.values()).map(s -> new TreeItem<>(s.getDisplayName())).forEach(root.getChildren()::add);
		treeView.setRoot(root);
		treeView.setShowRoot(false);
		treeView.getSelectionModel().selectFirst();
		treeView.getSelectionModel().selectedItemProperty().addListener(this::onSelectedItemChanged);
	}

	private void createTablePane() {
		isEnableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(isEnableColumn));
		keyColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
	}

	@FXML
	private void onApplyButtonClick(final ActionEvent event) {
		apply();
	}

	@FXML
	private void onApplyAndCloseButtonClick(final ActionEvent event) {
		apply();
		close(event);
	}

	@FXML
	private void onCloseButtonClick(final ActionEvent event) {
		close(event);
	}

	private void loadProperties(final String properties) throws IOException {
		final Optional<String> path = Optional.ofNullable(DSProperties.of(properties).getPath());
		if (!path.isPresent()) {
			layoutRoot.setVisible(false);
			return;
		}
		manager = new DSPropertyManager();
		layoutRoot.setVisible(true);
		try {
			manager.load(Paths.get(dsSetting.getExecutionPath(), path.get()));
			final List<DSProperty> list = manager.generateProperties().stream()
					.map(p -> new DSProperty(p, this::onPropertyChanged)).collect(Collectors.toList());
			tableView.getItems().setAll(list);
		} catch (final IOException e) {
			layoutRoot.setVisible(false);
			DialogUtil.showWarningDialog(properties + "が見つかりませんでした。");
		}
	}

	private void onSelectedItemChanged(final ObservableValue<? extends TreeItem<String>> observable,
			final TreeItem<String> oldValue, final TreeItem<String> newValue) {
		ConcurrentUtil.run(() -> loadProperties(newValue.getValue()));
	}

	private void onPropertyChanged(final ObservableValue<? extends String> observable, final String oldValue,
			final String newValue) {
		manager.replace(oldValue, newValue);
	}

	private void apply() {
		ConcurrentUtil.run(manager::write);
	}

	private void close(final ActionEvent event) {
		FXUtil.getStage(event).close();
	}

}
