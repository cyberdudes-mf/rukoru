package hoshisugi.rukoru.app.view.content;

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
import hoshisugi.rukoru.flamework.controls.BaseController;
import hoshisugi.rukoru.flamework.controls.ButtonTableCell;
import hoshisugi.rukoru.flamework.controls.StateTableCell;
import hoshisugi.rukoru.flamework.util.ConcurrentUtil;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class EC2InstanceTabController extends BaseController {

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
	private TableColumn<EC2Instance, String> operationColumn;

	@FXML
	private TableColumn<EC2Instance, String> launchTimeColumn;

	@FXML
	private TableColumn<EC2Instance, Boolean> autoStopColumn;

	@Inject
	private AuthService authService;

	@Inject
	private EC2Service ec2Service;

	@Override
	public void initialize(final URL url, final ResourceBundle resource) {
		stateColumn.setCellFactory(StateTableCell.forTableCellFactory());
		publicIpAddressColumn.setCellFactory(ButtonTableCell.forTableCellFactory(this::copyClipboard));
		autoStopColumn.setCellFactory(CheckBoxTableCell.forTableColumn(autoStopColumn));
		ConcurrentUtil.run(this::loadInstances);
	}

	private void loadInstances() {
		final Optional<AuthSetting> optional = authService.load();
		if (optional.isPresent()) {
			final AuthSetting authSetting = authService.load().get();
			final List<EC2Instance> instances = ec2Service.listInstances(authSetting);
			instances.stream().forEach(i -> i.autoStopProperty().addListener(this::onAutoStopChanged));
			Platform.runLater(() -> {
				tableView.getItems().addAll(FXCollections.observableArrayList(instances));
			});
		}
	}

	private void copyClipboard(final ActionEvent event) {
		final Button button = (Button) event.getSource();
		final ClipboardContent content = new ClipboardContent();
		content.putString(button.getText());
		Clipboard.getSystemClipboard().setContent(content);
	}

	private void onAutoStopChanged(final ObservableValue<? extends Boolean> observable, final Boolean oldValue,
			final Boolean newValue) {
		final Optional<AuthSetting> optional = authService.load();
		if (optional.isPresent()) {
			final BooleanProperty property = (BooleanProperty) observable;
			final AuthSetting authSetting = authService.load().get();
			final EC2Instance instance = (EC2Instance) property.getBean();
			final Map<String, String> tags = Map.of("AutoStop", Boolean.toString(property.get()).toUpperCase());
			ec2Service.updateTags(authSetting, instance, tags);
		}
	}
}
