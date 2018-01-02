package hoshisugi.rukoru.app.view.popup;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.enums.InstanceType;
import hoshisugi.rukoru.app.models.AuthSetting;
import hoshisugi.rukoru.app.models.CreateInstanceRequest;
import hoshisugi.rukoru.app.models.EC2Instance;
import hoshisugi.rukoru.app.models.MachineImage;
import hoshisugi.rukoru.app.models.Tag;
import hoshisugi.rukoru.app.services.ec2.EC2Service;
import hoshisugi.rukoru.app.view.content.InstanceTabController;
import hoshisugi.rukoru.flamework.annotations.FXController;
import hoshisugi.rukoru.flamework.controls.BaseController;
import hoshisugi.rukoru.flamework.controls.PropertyListCell;
import hoshisugi.rukoru.flamework.util.AssetUtil;
import hoshisugi.rukoru.flamework.util.ConcurrentUtil;
import hoshisugi.rukoru.flamework.util.DialogUtil;
import hoshisugi.rukoru.flamework.util.FXUtil;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

@FXController(title = "インスタンス設定")
public class CreateInstanceController extends BaseController {

	@FXML
	private TextField name;

	@FXML
	private ComboBox<InstanceType> instanceType;

	@FXML
	private TableView<Tag> tagTable;

	@FXML
	private TableColumn<Tag, String> keyColumn;

	@FXML
	private TableColumn<Tag, String> valueColumn;

	@FXML
	private Button addButton;

	@FXML
	private Button removeButton;

	@FXML
	private Button okButton;

	@Inject
	private InstanceTabController instanceController;

	@Inject
	private EC2Service ec2Service;

	private final ObjectProperty<MachineImage> target = new SimpleObjectProperty<>(this, "target");

	public ObjectProperty<MachineImage> targetProperty() {
		return target;
	}

	@Override
	public void initialize(final URL url, final ResourceBundle resource) {
		target.addListener((observable, oldValue, newValue) -> {
			name.setText(newValue.getName());
		});
		instanceType.getItems().addAll(InstanceType.values());
		instanceType.setCellFactory(PropertyListCell.forListView(InstanceType::getDisplayName));
		instanceType.setButtonCell(instanceType.getCellFactory().call(null));
		instanceType.getSelectionModel().select(InstanceType.T2Micro);
		keyColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		addButton.setGraphic(new ImageView(AssetUtil.getImage("16x16/navigate_plus.png")));
		removeButton.setGraphic(new ImageView(AssetUtil.getImage("16x16/navigate_minus.png")));
		removeButton.disableProperty().bind(tagTable.getSelectionModel().selectedItemProperty().isNull());
		okButton.disableProperty().bind(Bindings.isEmpty(name.textProperty()));
		initTagTable();
	}

	private void initTagTable() {
		final ObservableList<Tag> items = tagTable.getItems();
		items.add(new Tag("Name", ""));
		items.add(new Tag("SpiderInstance", ""));
		items.add(new Tag("Type", "QA"));
		items.add(new Tag("AutoStop", Boolean.TRUE.toString().toUpperCase()));
		items.stream().filter(t -> t.getKey().equals("Name"))
				.forEach(t -> t.valueProperty().bindBidirectional(name.textProperty()));
	}

	@FXML
	private void onOKButtonClick(final ActionEvent event) {
		final ObservableList<Tag> tags = tagTable.getItems();
		if (tags.stream().noneMatch(t -> t.getKey().equals("Name"))) {
			tags.add(new Tag("Name", name.getText()));
		}
		if (tags.stream().noneMatch(t -> t.getKey().equals("SpiderInstance"))) {
			tags.add(new Tag("SpiderInstance", ""));
		}

		ConcurrentUtil.run(() -> {

			if (!AuthSetting.hasSetting()) {
				DialogUtil.showWarningDialog("警告", "認証情報を設定してください。\n[メニュー] - [Settings] - [認証設定]");
				return;
			}

			final CreateInstanceRequest request = createRequest(tags);
			final List<EC2Instance> instances = ec2Service.createInstance(request);
			instanceController.getItems().addAll(0, instances);

			Platform.runLater(() -> {
				DialogUtil.showInfoDialog("インスタンス作成", String.format("[%s] のインスタンス作成を受け付けました。", name.getText()));
				close(FXUtil.getStage(event));
			});
		});
	}

	private CreateInstanceRequest createRequest(final ObservableList<Tag> tags) {
		final CreateInstanceRequest request = new CreateInstanceRequest();
		final MachineImage entity = target.get();
		request.setImageId(entity.getImageId());
		request.setInstanceType(instanceType.getSelectionModel().getSelectedItem());
		request.setMinCount(1);
		request.setMaxCount(1);
		request.setKeyName("keypair_common");
		request.setSecurityGroup("spider-instance");
		request.getTags().addAll(tags);
		return request;
	}

	@FXML
	private void onCancelButtonClick(final ActionEvent event) {
		close(FXUtil.getStage(event));
	}

	@FXML
	private void onAddButtonClick(final ActionEvent event) {
		final Tag newTag = new Tag();
		tagTable.getItems().add(newTag);
		tagTable.getSelectionModel().select(newTag);
	}

	@FXML
	private void onRemoveButtonClick(final ActionEvent event) {
		final Tag tag = tagTable.getSelectionModel().getSelectedItem();
		tagTable.getItems().removeAll(tag);
	}

	private void close(final Stage stage) {
		removeButton.disableProperty().unbind();
		okButton.disableProperty().unbind();
		stage.close();
	}
}
