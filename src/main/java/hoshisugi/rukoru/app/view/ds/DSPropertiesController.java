package hoshisugi.rukoru.app.view.ds;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import hoshisugi.rukoru.app.enums.DSPropertiesGroup;
import hoshisugi.rukoru.app.models.ds.DSProperties;
import hoshisugi.rukoru.app.models.ds.DSPropertiesContent.Property;
import hoshisugi.rukoru.app.models.ds.DSPropertiesTreeNode;
import hoshisugi.rukoru.app.models.ds.DSSetting;
import hoshisugi.rukoru.framework.annotations.FXController;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.util.AssetUtil;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import hoshisugi.rukoru.framework.util.DialogUtil;
import hoshisugi.rukoru.framework.util.FXUtil;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
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
	private TreeView<DSPropertiesTreeNode> treeView;

	@FXML
	private VBox layoutRoot;

	@FXML
	private TableView<Property> tableView;

	@FXML
	private TableColumn<Property, Boolean> enableColumn;

	@FXML
	private TableColumn<Property, String> keyColumn;

	@FXML
	private TableColumn<Property, String> valueColumn;

	@FXML
	private Button addButton;

	@FXML
	private Button deleteButton;

	@FXML
	private Button CloseButton;

	public void setDSSetting(final DSSetting dsSetting) {
		createTree(dsSetting);
	}

	@Override
	public void initialize(final URL url, final ResourceBundle resource) {
		createTablePane();
		addButton.setGraphic(new ImageView(AssetUtil.getImage("24x24/add.png")));
		deleteButton.setGraphic(new ImageView(AssetUtil.getImage("24x24/delete.png")));
		deleteButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
	}

	private void createTree(final DSSetting dsSetting) {
		final TreeItem<DSPropertiesTreeNode> root = new TreeItem<>();
		for (final DSPropertiesGroup group : DSPropertiesGroup.values()) {
			final TreeItem<DSPropertiesTreeNode> groupNode = new TreeItem<>(group);
			groupNode.setExpanded(true);
			final ObservableList<TreeItem<DSPropertiesTreeNode>> children = groupNode.getChildren();
			final List<DSProperties> files = group.loadFiles(dsSetting);
			if (!files.isEmpty()) {
				group.loadFiles(dsSetting).stream().map(TreeItem<DSPropertiesTreeNode>::new).forEach(children::add);
				root.getChildren().add(groupNode);
			}
		}
		treeView.setRoot(root);
		treeView.getSelectionModel().selectedItemProperty().addListener(this::onSelectedItemChanged);
	}

	private void createTablePane() {
		layoutRoot.visibleProperty().bind(treeView.getSelectionModel().selectedItemProperty().isNotNull());
		enableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(enableColumn));
		keyColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
	}

	@FXML
	private void onAddButtonClick(final ActionEvent event) {
		final Optional<DSProperties> properties = getSelectedProperties();
		properties.ifPresent(p -> {
			final Property item = p.add("key", "value");
			tableView.getItems().add(item);
		});
	}

	@FXML
	private void onDeleteButtonClick(final ActionEvent event) {
		final Property item = tableView.getSelectionModel().getSelectedItem();
		final Optional<DSProperties> properties = getSelectedProperties();
		properties.ifPresent(p -> {
			p.remove(item.getKey());
			tableView.getItems().remove(item);
		});
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

	private void loadProperties(final DSProperties properties) throws IOException {
		try {
			final List<Property> items = properties.loadProperties();
			tableView.getItems().setAll(items);
		} catch (final IOException e) {
			layoutRoot.setVisible(false);
			DialogUtil.showWarningDialog(properties + "が見つかりませんでした。");
		}
	}

	private void onSelectedItemChanged(final ObservableValue<? extends TreeItem<DSPropertiesTreeNode>> observable,
			final TreeItem<DSPropertiesTreeNode> oldValue, final TreeItem<DSPropertiesTreeNode> newValue) {
		if (newValue != null) {
			final DSPropertiesTreeNode node = newValue.getValue();
			if (node instanceof DSProperties) {
				ConcurrentUtil.run(() -> loadProperties((DSProperties) node));
			}
		}
	}

	private void apply() {
		getSelectedProperties().ifPresent(p -> ConcurrentUtil.run(p::save));
	}

	private void close(final ActionEvent event) {
		FXUtil.getStage(event).close();
	}

	private Optional<DSProperties> getSelectedProperties() {
		final DSPropertiesTreeNode node = treeView.getSelectionModel().getSelectedItem().getValue();
		if (node instanceof DSProperties) {
			return Optional.of((DSProperties) node);
		} else {
			return Optional.empty();
		}
	}
}
