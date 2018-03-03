package hoshisugi.rukoru.app.view.settings;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import hoshisugi.rukoru.app.models.settings.DSSetting;
import hoshisugi.rukoru.framework.annotations.FXController;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.util.AssetUtil;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import hoshisugi.rukoru.framework.util.DialogUtil;
import hoshisugi.rukoru.framework.util.FXUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;

@FXController(title = "DSSetting")
public class DSSettingsController extends BaseController implements PreferenceContent {

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
	private TableColumn<DSSetting, String> executionTypeColumn;

	private final ObservableList<DSSetting> items = FXCollections.observableArrayList();

	public ObservableList<DSSetting> getItems() {
		return items;
	}

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		createButton.setGraphic(new ImageView(AssetUtil.getImage("24x24/add.png")));
		deleteButton.setGraphic(new ImageView(AssetUtil.getImage("24x24/delete.png")));
		deleteButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
		tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		tableView.setItems(items);
	}

	@Override
	public void apply() {
	}

	@FXML
	private void onEntryButtonClick(final ActionEvent event) {
		FXUtil.popup(EntryDSController.class, FXUtil.getStage(event));
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

		ConcurrentUtil.run(() -> {
			items.removeAll(selectedItems);
		});
	}

	@FXML
	private void onApplyButtonClick(final ActionEvent e) {
		apply();
	}

	void add(final DSSetting setting) {
		items.add(setting);
	}
}
