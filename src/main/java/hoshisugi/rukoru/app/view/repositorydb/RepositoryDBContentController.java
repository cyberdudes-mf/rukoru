package hoshisugi.rukoru.app.view.repositorydb;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.google.common.base.Strings;
import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.auth.AuthSetting;
import hoshisugi.rukoru.app.models.repositorydb.RepositoryDB;
import hoshisugi.rukoru.app.services.repositorydb.RepositoryDBService;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.controls.GraphicTableCell;
import hoshisugi.rukoru.framework.util.AssetUtil;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import hoshisugi.rukoru.framework.util.DialogUtil;
import hoshisugi.rukoru.framework.util.FXUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
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

	private ObservableList<RepositoryDB> selectedItems;

	public ObservableList<RepositoryDB> getItems() {
		return items;
	}

	@Override
	public void initialize(final URL arg0, final ResourceBundle arg1) {
		copyButtonColumn.setCellValueFactory(GraphicTableCell.forTableCellValueFactory());
		copyButtonColumn.setCellFactory(GraphicTableCell.forTableCellFactory(this::createCopyButton));
		refreshButton.setGraphic(new ImageView(AssetUtil.getImage("24x24/refresh.png")));
		createButton.setGraphic(new ImageView(AssetUtil.getImage("24x24/add.png")));
		deleteButton.setGraphic(new ImageView(AssetUtil.getImage("24x24/delete.png")));
		tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		selectedItems = tableView.getSelectionModel().getSelectedItems();
		selectedItems.addListener(this::onSelectedItemsChanged);
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
				if (AuthSetting.hasSetting()) {
					final String dbName = repositorydbNameOptional.get();
					service.createRepositoryDB(dbName);
					final RepositoryDB db = new RepositoryDB();
					db.setName(dbName);
					Platform.runLater(() -> {
						items.add(0, db);
					});
				}
			});
		}
	}

	@FXML
	private void onDeleteButtonClick() {
		final Optional<ButtonType> result = DialogUtil.showConfirmDialog("リポジトリDB削除",
				selectedItems.size() > 1 ? String.format("選択された[%d] 個の削除します。よろしいですか？", selectedItems.size())
						: String.format("[%s] を削除します。よろしいですか？", selectedItems.get(0).getName()));
		if (!result.map(type -> type == ButtonType.OK).orElse(false)) {
			return;
		}

		ConcurrentUtil.run(() -> {
			if (AuthSetting.hasSetting()) {
				selectedItems.forEach(db -> {
					try {
						service.dropRepositoryDB(db.getName());
					} catch (final SQLException e) {
						e.printStackTrace();
					}
				});
				items.removeAll(selectedItems);
			}
		});
	}

	private void loadRepositoryDB() throws SQLException {
		try {
			Platform.runLater(() -> refreshButton.setDisable(true));
			Platform.runLater(() -> createButton.setDisable(true));
			items.clear();
			if (AuthSetting.hasSetting()) {
				items.addAll(service.listRepositoryDB());
				Platform.runLater(() -> createButton.setDisable(false));
			}
		} finally {
			Platform.runLater(() -> refreshButton.setDisable(false));
		}
	}

	private Button createCopyButton(final RepositoryDB db) {
		if (Strings.isNullOrEmpty(db.getName())) {
			return null;
		}
		final Button button = new Button();
		button.setGraphic(new ImageView(AssetUtil.getImage("16x16/clipboard.png")));
		button.setOnAction(this::onCopyButtonClick);
		button.setUserData(db);
		return button;
	}

	private void onCopyButtonClick(final ActionEvent event) {
		final Button button = (Button) event.getSource();
		final ClipboardContent content = new ClipboardContent();
		final RepositoryDB db = (RepositoryDB) button.getUserData();
		content.putString(db.getName());
		Clipboard.getSystemClipboard().setContent(content);
		final Tooltip tooltip = new Tooltip("クリップボードにコピーしました。");
		tooltip.setAutoHide(true);
		tooltip.show(FXUtil.getStage(event));
		final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.schedule(() -> {
			Platform.runLater(() -> tooltip.hide());
			scheduler.shutdown();
		}, 1000, MILLISECONDS);
	}

	private void onSelectedItemsChanged(final Change<? extends RepositoryDB> change) {
		if (selectedItems.size() > 0) {
			deleteButton.setDisable(false);
		} else {
			deleteButton.setDisable(true);
		}
	}

}
