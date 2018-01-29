package hoshisugi.rukoru.app.view.ec2;

import static hoshisugi.rukoru.framework.util.AssetUtil.getImage;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static javafx.beans.binding.Bindings.when;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import com.google.inject.Inject;

import hoshisugi.rukoru.app.enums.EC2InstanceState;
import hoshisugi.rukoru.app.models.ec2.EC2Instance;
import hoshisugi.rukoru.app.models.settings.Credential;
import hoshisugi.rukoru.app.services.ec2.EC2Service;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.controls.GraphicTableCell;
import hoshisugi.rukoru.framework.controls.TextFillTableCell;
import hoshisugi.rukoru.framework.util.AssetUtil;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import hoshisugi.rukoru.framework.util.DialogUtil;
import hoshisugi.rukoru.framework.util.FXUtil;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.paint.Color;

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
	private TableColumn<EC2Instance, EC2Instance> publicIpAddressColumn;

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
	private EC2Service ec2Service;

	private final ObservableList<EC2Instance> items = FXCollections.observableArrayList();

	public ObservableList<EC2Instance> getItems() {
		return items;
	}

	@Override
	public void initialize(final URL url, final ResourceBundle resource) {
		stateColumn.setCellFactory(TextFillTableCell.forTableCellFactory(this::provideColor));
		publicIpAddressColumn.setCellValueFactory(GraphicTableCell.forTableCellValueFactory());
		publicIpAddressColumn.setCellFactory(GraphicTableCell.forTableCellFactory(this::createPublicIpAddressButton));
		autoStopColumn.setCellFactory(CheckBoxTableCell.forTableColumn(autoStopColumn));
		runAndStopColumn.setCellValueFactory(GraphicTableCell.forTableCellValueFactory());
		runAndStopColumn.setCellFactory(GraphicTableCell.forTableCellFactory(this::createRunAndStopButton));
		deleteColumn.setCellValueFactory(GraphicTableCell.forTableCellValueFactory());
		deleteColumn.setCellFactory(GraphicTableCell.forTableCellFactory(this::createDeleteButton));
		imageColumn.setCellValueFactory(GraphicTableCell.forTableCellValueFactory());
		imageColumn.setCellFactory(GraphicTableCell.forTableCellFactory(this::createImageButton));
		refreshButton.setGraphic(new ImageView(AssetUtil.getImage("24x24/refresh.png")));
		tableView.setItems(items);
		items.addListener(this::onItemsChanged);
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
			if (Credential.hasCredential()) {
				final List<EC2Instance> instances = ec2Service.listInstances();
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
		final Tooltip tooltip = new Tooltip("クリップボードにコピーしました。");
		tooltip.setAutoHide(true);
		tooltip.show(FXUtil.getStage(event));
		final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.schedule(() -> {
			Platform.runLater(() -> tooltip.hide());
			scheduler.shutdown();
		}, 1000, MILLISECONDS);
	}

	private Button createImageButton(final EC2Instance entity) {
		final Button button = new Button("イメージ");
		final StringProperty state = entity.stateProperty();
		button.disableProperty().bind(state.isEqualTo("terminated"));
		button.setGraphic(new ImageView(AssetUtil.getImage("16x16/image.png")));
		button.setOnAction(this::onCreateImageButtonClick);
		button.setUserData(entity);
		return button;
	}

	private Button createDeleteButton(final EC2Instance entity) {
		final Button button = new Button("削除");
		final StringProperty state = entity.stateProperty();
		button.disableProperty().bind(state.isEqualTo("terminated"));
		button.setGraphic(new ImageView(AssetUtil.getImage("16x16/delete.png")));
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

	private Button createPublicIpAddressButton(final EC2Instance entity) {
		if (Strings.isNullOrEmpty(entity.getPublicIpAddress())) {
			return null;
		}
		final Button button = new Button();
		button.setText(entity.getPublicIpAddress());
		button.setOnAction(this::onCopyButtonClick);
		button.setUserData(entity);
		return button;
	}

	private ImageView createRunAndStopButtonImage(final StringProperty state) {
		final ImageView imageView = new ImageView();
		imageView.imageProperty().bind(
				when(state.isEqualTo("stopped")).then(getImage("16x16/run.png")).otherwise(getImage("16x16/stop.png")));
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
			if (Credential.hasCredential()) {
				if (start) {
					ec2Service.startInstance(instance);
				} else {
					ec2Service.stopInstance(instance);
				}
				ec2Service.monitorInstances(Arrays.asList(instance));
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
			if (Credential.hasCredential()) {
				ec2Service.terminateInstance(instance);
				ec2Service.monitorInstances(Arrays.asList(instance));
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
			if (Credential.hasCredential()) {
				final BooleanProperty property = (BooleanProperty) observable;
				final EC2Instance instance = (EC2Instance) property.getBean();
				final Map<String, String> tags = new HashMap<>();
				tags.put("AutoStop", Boolean.toString(property.get()));
				ec2Service.updateTags(instance, tags);
			}
		});
	}

	private void onItemsChanged(final Change<? extends EC2Instance> change) {
		// TODO change を回さないとダメ
		if (items.stream().noneMatch(EC2Service::needMonitoring)) {
			return;
		}
		ConcurrentUtil.run(() -> {
			if (Credential.hasCredential()) {
				final List<EC2Instance> instances = items.stream().filter(EC2Service::needMonitoring)
						.collect(Collectors.toList());
				ec2Service.monitorInstances(instances);
			}
		});
	}

	private Color provideColor(final String state) {
		final EC2InstanceState s = EC2InstanceState.of(state);
		final Color color;
		switch (s) {
		case Running:
			color = Color.GREEN;
			break;
		case Stopped:
			color = Color.RED;
			break;
		case Pending:
		case Stopping:
		case ShuttingDown:
			color = Color.GOLDENROD;
			break;
		case Terminated:
			color = Color.PURPLE;
			break;
		default:
			color = null;
		}
		return color;
	}
}
