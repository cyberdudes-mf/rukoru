package hoshisugi.rukoru.app.view.auth;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.auth.AuthSetting;
import hoshisugi.rukoru.app.models.rds.RDSInstance;
import hoshisugi.rukoru.app.services.rds.RDSService;
import hoshisugi.rukoru.framework.annotations.FXController;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.controls.PropertyListCell;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import hoshisugi.rukoru.framework.util.FXUtil;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

@FXController(title = "リポジトリDB設定")
public class RepositoryDBSettingController extends BaseController {

	@FXML
	private ComboBox<RDSInstance> instance;

	@FXML
	private TextField endpoint;

	@FXML
	private TextField port;

	@FXML
	private TextField username;

	@FXML
	private PasswordField password;

	@FXML
	private Button saveButton;

	@Inject
	private RDSService rdsService;

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		instance.setCellFactory(PropertyListCell.forListView(RDSInstance::getInstanceName));
		instance.setButtonCell(instance.getCellFactory().call(null));
		instance.getSelectionModel().selectedItemProperty().addListener(this::onInstanceSelectionChanged);
		saveButton.disableProperty().bind(Bindings.or(instance.getSelectionModel().selectedItemProperty().isNull(),
				password.textProperty().isEmpty()));
		loadInstances();
	}

	private void loadInstances() {
		ConcurrentUtil.run(() -> {
			if (AuthSetting.hasSetting()) {
				instance.getItems().setAll(rdsService.listInstances());
				Platform.runLater(instance.getSelectionModel()::selectFirst);
			}
		});
	}

	@FXML
	private void onSaveButtonClick(final ActionEvent event) {
		FXUtil.getStage(event).close();
	}

	@FXML
	private void onCancelButtonClick(final ActionEvent event) {
		FXUtil.getStage(event).close();
	}

	private void onInstanceSelectionChanged(final ObservableValue<? extends RDSInstance> observable,
			final RDSInstance oldValue, final RDSInstance newValue) {
		if (newValue != null) {
			endpoint.textProperty().bind(newValue.endpointProperty());
			port.textProperty().bind(newValue.portProperty().asString());
			username.textProperty().bind(newValue.usernameProperty());
		}
	}
}
