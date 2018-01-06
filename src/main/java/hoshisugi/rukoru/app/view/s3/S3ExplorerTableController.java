package hoshisugi.rukoru.app.view.s3;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.s3.S3Item;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.controls.GraphicTableCell;
import hoshisugi.rukoru.framework.util.FXUtil;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class S3ExplorerTableController extends BaseController {

	@FXML
	private TableView<S3Item> tableView;

	@FXML
	private TableColumn<S3Item, S3Item> iconColumn;

	@FXML
	private TableColumn<S3Item, String> nameColumn;

	@FXML
	private TableColumn<S3Item, String> lastModifiedColumn;

	@FXML
	private TableColumn<S3Item, String> sizeColumn;

	@FXML
	private TableColumn<S3Item, String> storageClassColumn;

	@Inject
	private S3ExplorerController explorer;

	@Override
	public void initialize(final URL url, final ResourceBundle resource) {
		iconColumn.setCellValueFactory(GraphicTableCell.forTableCellValueFactory());
		iconColumn.setCellFactory(GraphicTableCell.forTableCellFactory(this::createIcon));
		explorer.selectedItemProperty().addListener(this::selectedItemChanged);
		tableView.setOnMouseClicked(this::onTableViewClicked);
	}

	private void onTableViewClicked(final MouseEvent event) {
		if (FXUtil.isDoubleClicked(event)) {
			final S3Item item = tableView.getSelectionModel().getSelectedItem();
			if (item != null && item.isContainer()) {
				explorer.setSelectedItem(item);
			}
		}
	}

	private void selectedItemChanged(final ObservableValue<? extends S3Item> observable, final S3Item oldValue,
			final S3Item newValue) {
		if (newValue != null) {
			tableView.setItems(newValue.getItems());
		} else {
			tableView.getItems().clear();
		}
	}

	private ImageView createIcon(final S3Item item) {
		return new ImageView(item.getIcon());
	}

}
