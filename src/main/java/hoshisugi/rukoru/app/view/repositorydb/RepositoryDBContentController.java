package hoshisugi.rukoru.app.view.repositorydb;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import com.google.common.base.Strings;
import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.repositorydb.RepositoryDB;
import hoshisugi.rukoru.app.models.settings.Credential;
import hoshisugi.rukoru.app.models.settings.RepositoryDBConnection;
import hoshisugi.rukoru.app.services.repositorydb.RepositoryDBService;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.controls.GraphicTableCell;
import hoshisugi.rukoru.framework.util.AssetUtil;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import hoshisugi.rukoru.framework.util.DialogUtil;
import hoshisugi.rukoru.framework.util.FXUtil;
import javafx.application.Platform;
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
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class RepositoryDBContentController extends BaseController {

	@FXML
	private TableView<RepositoryDB> tableView;

	@FXML
	private TableColumn<RepositoryDB, String> nameColumn;

	@FXML
	private TableColumn<RepositoryDB, RepositoryDB> copyButtonColumn;

	@FXML
	private Button refreshButton;

	@FXML
	private Button createButton;

	@FXML
	private Button deleteButton;

	@Inject
	private RepositoryDBService service;

	private final ObservableList<RepositoryDB> items = FXCollections.observableArrayList();

	@Override
	public void initialize(final URL arg0, final ResourceBundle arg1) {
		copyButtonColumn.setCellValueFactory(GraphicTableCell.forTableCellValueFactory());
		copyButtonColumn.setCellFactory(GraphicTableCell.forTableCellFactory(this::createCopyToClipboardButton));
		refreshButton.setGraphic(new ImageView(AssetUtil.getImage("24x24/refresh.png")));
		createButton.setGraphic(new ImageView(AssetUtil.getImage("24x24/add.png")));
		deleteButton.setGraphic(new ImageView(AssetUtil.getImage("24x24/delete.png")));
		deleteButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
		tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		tableView.setItems(items);
		ConcurrentUtil.run(this::loadRepositoryDB);
	}

	@FXML
	private void onRefreshButtonClick() {
		ConcurrentUtil.run(this::loadRepositoryDB);
	}

	@FXML
	private void onCreateButtonClick() {
		final Optional<String> repositorydbNameOptional = DialogUtil.showTextInputDialog("リポジトリDB作成", "リポジトリ名");
		if (repositorydbNameOptional.isPresent()) {
			ConcurrentUtil.run(() -> {
				if (Credential.hasCredential() && RepositoryDBConnection.hasConnection()) {
					final String dbName = repositorydbNameOptional.get();
					final RepositoryDB db = service.createRepositoryDB(dbName);
					Platform.runLater(() -> {
						items.add(db);
						tableView.getSelectionModel().select(db);
						tableView.scrollTo(db);
					});
				}
			});
		}
	}

	@FXML
	private void onDeleteButtonClick() {
		final ObservableList<RepositoryDB> selectedItems = tableView.getSelectionModel().getSelectedItems();
		final Optional<ButtonType> result = DialogUtil.showConfirmDialog("リポジトリDB削除",
				selectedItems.size() > 1 ? String.format("選択された[%d] 個のデータベースを削除します。よろしいですか？", selectedItems.size())
						: String.format("[%s] を削除します。よろしいですか？", selectedItems.get(0).getName()));
		if (!result.map(type -> type == ButtonType.OK).orElse(false)) {
			return;
		}

		ConcurrentUtil.run(() -> {
			if (Credential.hasCredential() && RepositoryDBConnection.hasConnection()) {
				for (final RepositoryDB db : selectedItems) {
					service.dropRepositoryDB(db.getName());
					items.remove(db);
				}
			}
		});
	}

	private void loadRepositoryDB() throws SQLException {
		try {
			Platform.runLater(() -> {
				refreshButton.setDisable(true);
				createButton.setDisable(true);
			});
			items.clear();
			if (Credential.hasCredential() && RepositoryDBConnection.hasConnection()) {
				items.addAll(service.listRepositoryDB());
				Platform.runLater(() -> createButton.setDisable(false));
			}
		} finally {
			Platform.runLater(() -> refreshButton.setDisable(false));
		}
	}

	private Button createCopyToClipboardButton(final RepositoryDB db) {
		if (Strings.isNullOrEmpty(db.getName())) {
			return null;
		}
		final Button button = new Button();
		button.setGraphic(new ImageView(AssetUtil.getImage("16x16/clipboard.png")));
		button.setOnAction(this::onCopyToClipboardButtonClick);
		button.setUserData(db);
		return button;
	}

	private void onCopyToClipboardButtonClick(final ActionEvent event) {
		final Button button = (Button) event.getSource();
		final ClipboardContent content = new ClipboardContent();
		final RepositoryDB db = (RepositoryDB) button.getUserData();
		content.putString(db.getName());
		Clipboard.getSystemClipboard().setContent(content);
		FXUtil.showTooltip("クリップボードにコピーしました。", event);
	}
}
