package hoshisugi.rukoru.app.view.content;

import static hoshisugi.rukoru.flamework.util.AssetUtil.getImage;
import static javafx.beans.binding.Bindings.when;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.AuthSetting;
import hoshisugi.rukoru.app.models.EC2Instance;
import hoshisugi.rukoru.app.services.auth.AuthService;
import hoshisugi.rukoru.app.services.ec2.EC2Service;
import hoshisugi.rukoru.app.view.popup.CreateImageController;
import hoshisugi.rukoru.flamework.controls.BaseController;
import hoshisugi.rukoru.flamework.controls.ButtonTableCell;
import hoshisugi.rukoru.flamework.controls.GraphicTableCell;
import hoshisugi.rukoru.flamework.controls.StateTableCell;
import hoshisugi.rukoru.flamework.util.AssetUtil;
import hoshisugi.rukoru.flamework.util.ConcurrentUtil;
import hoshisugi.rukoru.flamework.util.DialogUtil;
import hoshisugi.rukoru.flamework.util.FXUtil;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class InstanceTabController extends BaseController {

	@FXML
	private TableView<EC2Instance> tableView;

	@FXML
	private TableColumn<EC2Instance, String> nameColumn;

	@FXML
	private TableColumn<EC2Instance, String> instanceTypeColumn;

	@FXML
	private TableColumn<EC2Instance, String> stateColumn;

	@FXML
	private TableColumn<EC2Instance, String> publicIpAddressColumn;

	@FXML
	private TableColumn<EC2Instance, EC2Instance> runAndStopColumn;

	@FXML
	private TableColumn<EC2Instance, String> launchTimeColumn;

	@FXML
	private TableColumn<EC2Instance, Boolean> autoStopColumn;

	@FXML
	private TableColumn<EC2Instance, EC2Instance> deleteColumn;

	@FXML
	private TableColumn<EC2Instance, EC2Instance> imageColumn;

	@FXML
	private Button refreshButton;

	@Inject
	private AuthService authService;

	@Inject
	private EC2Service ec2Service;

	private final ObservableList<EC2Instance> items = FXCollections.observableArrayList();

	public ObservableList<EC2Instance> getItems() {
		return items;
	}

	@Override
	public void initialize(final URL url, final ResourceBundle resource) {
		stateColumn.setCellFactory(StateTableCell.forTableCellFactory());
		publicIpAddressColumn.setCellFactory(ButtonTableCell.forTableCellFactory(this::onCopyButtonClick));
		autoStopColumn.setCellFactory(CheckBoxTableCell.forTableColumn(autoStopColumn));
		runAndStopColumn.setCellValueFactory(GraphicTableCell.forTableCellValueFactory());
		runAndStopColumn.setCellFactory(GraphicTableCell.forTableCellFactory(this::createRunAndStopButton));
		deleteColumn.setCellValueFactory(GraphicTableCell.forTableCellValueFactory());
		deleteColumn.setCellFactory(GraphicTableCell.forTableCellFactory(this::createDeleteButton));
		imageColumn.setCellValueFactory(GraphicTableCell.forTableCellValueFactory());
		imageColumn.setCellFactory(GraphicTableCell.forTableCellFactory(this::createImageButton));
		refreshButton.setGraphic(new ImageView(AssetUtil.getImage("refresh_24x24.png")));
		tableView.setItems(items);
		ConcurrentUtil.run(this::loadInstances);
	}

	@FXML
	private void onRefreshButtonClick(final ActionEvent event) {
		ConcurrentUtil.run(this::loadInstances);
	}

	private void loadInstances() {
		try {
			Platform.runLater(() -> refreshButton.setDisable(true));
			items.stream().forEach(i -> i.autoStopProperty().removeListener(this::onAutoStopChanged));
			items.clear();
			final Optional<AuthSetting> optional = authService.load();
			if (optional.isPresent()) {
				final AuthSetting authSetting = optional.get();
				final List<EC2Instance> instances = ec2Service.listInstances(authSetting);
				instances.stream().forEach(i -> i.autoStopProperty().addListener(this::onAutoStopChanged));
				items.addAll(instances);
			}
		} finally {
			Platform.runLater(() -> refreshButton.setDisable(false));
		}
	}

	private void onCopyButtonClick(final ActionEvent event) {
		final Button button = (Button) event.getSource();
		final ClipboardContent content = new ClipboardContent();
		content.putString(button.getText());
		Clipboard.getSystemClipboard().setContent(content);
	}

	private Button createImageButton(final EC2Instance entity) {
		final Button button = new Button("イメージ");
		final StringProperty state = entity.stateProperty();
		button.disableProperty().bind(state.isEqualTo("terminated"));
		button.setGraphic(new ImageView(AssetUtil.getImage("image_16x16.png")));
		button.setOnAction(this::onCreateImageButtonClick);
		button.setUserData(entity);
		return button;
	}

	private Button createDeleteButton(final EC2Instance entity) {
		final Button button = new Button("削除");
		final StringProperty state = entity.stateProperty();
		button.disableProperty().bind(state.isEqualTo("terminated"));
		button.setGraphic(new ImageView(AssetUtil.getImage("delete_16x16.png")));
		button.setOnAction(this::onDeleteButtonClick);
		button.setUserData(entity);
		return button;
	}

	private Button createRunAndStopButton(final EC2Instance entity) {
		final Button button = new Button();
		final StringProperty state = entity.stateProperty();
		button.textProperty().bind(createRunAndStopButtonText(state));
		button.disableProperty().bind(createRunAndStopButtonDisable(state));
		button.setGraphic(createRunAndStopButtonImage(state));
		button.setOnAction(this::onRunAndStopButtonClick);
		button.setUserData(entity);
		return button;
	}

	private ImageView createRunAndStopButtonImage(final StringProperty state) {
		final ImageView imageView = new ImageView();
		imageView.imageProperty().bind(
				when(state.isEqualTo("stopped")).then(getImage("run_16x16.png")).otherwise(getImage("stop_16x16.png")));
		return imageView;
	}

	private StringBinding createRunAndStopButtonText(final StringProperty state) {
		return when(state.isEqualTo("stopped")).then("起動").otherwise("停止");
	}

	private BooleanBinding createRunAndStopButtonDisable(final StringProperty state) {
		return state.isNotEqualTo("running").and(state.isNotEqualTo("stopped"));
	}

	private void onRunAndStopButtonClick(final ActionEvent event) {
		ConcurrentUtil.run(() -> {
			final Button button = (Button) event.getSource();
			final EC2Instance instance = (EC2Instance) button.getUserData();
			final boolean start = instance.getState().equals("stopped");
			final Optional<AuthSetting> optional = authService.load();
			if (optional.isPresent()) {
				final AuthSetting authSetting = optional.get();
				if (start) {
					ec2Service.startInstance(authSetting, instance);
				} else {
					ec2Service.stopInstance(authSetting, instance);
				}
			}
		});
	}

	private void onDeleteButtonClick(final ActionEvent event) {
		final Button button = (Button) event.getSource();
		final EC2Instance instance = (EC2Instance) button.getUserData();

		final Optional<ButtonType> result = DialogUtil.showConfirmDialog("インスタンス削除",
				String.format("[%s] を削除します。よろしいですか？", instance.getName()));
		if (!result.map(type -> type == ButtonType.OK).orElse(false)) {
			return;
		}

		ConcurrentUtil.run(() -> {
			final Optional<AuthSetting> optional = authService.load();
			if (optional.isPresent()) {
				ec2Service.terminateInstance(optional.get(), instance);
			}
		});
	}

	private void onCreateImageButtonClick(final ActionEvent event) {
		final Button button = (Button) event.getSource();
		final EC2Instance instance = (EC2Instance) button.getUserData();
		final CreateImageController controller = FXUtil.popup(CreateImageController.class, FXUtil.getStage(event));
		controller.targetProperty().set(instance);
	}

	private void onAutoStopChanged(final ObservableValue<? extends Boolean> observable, final Boolean oldValue,
			final Boolean newValue) {
		ConcurrentUtil.run(() -> {
			final Optional<AuthSetting> optional = authService.load();
			if (optional.isPresent()) {
				final BooleanProperty property = (BooleanProperty) observable;
				final AuthSetting authSetting = optional.get();
				final EC2Instance instance = (EC2Instance) property.getBean();
				final Map<String, String> tags = Map.of("AutoStop", Boolean.toString(property.get()).toUpperCase());
				ec2Service.updateTags(authSetting, instance, tags);
			}
		});
	}
}
