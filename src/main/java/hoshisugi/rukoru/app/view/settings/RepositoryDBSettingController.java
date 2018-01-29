package hoshisugi.rukoru.app.view.settings;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.rds.RDSInstance;
import hoshisugi.rukoru.app.models.settings.Credential;
import hoshisugi.rukoru.app.models.settings.RepositoryDBConnection;
import hoshisugi.rukoru.app.services.rds.RDSService;
import hoshisugi.rukoru.app.services.settings.LocalSettingService;
import hoshisugi.rukoru.framework.annotations.FXController;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.controls.PropertyListCell;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import hoshisugi.rukoru.framework.util.DialogUtil;
import hoshisugi.rukoru.framework.util.FXUtil;
import javafx.application.Platform;
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

	@Inject
	private LocalSettingService settingService;

	private RepositoryDBConnection entity;

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		instance.setCellFactory(PropertyListCell.forListView(RDSInstance::getInstanceName));
		instance.setButtonCell(instance.getCellFactory().call(null));
		instance.getSelectionModel().selectedItemProperty().addListener(this::onInstanceSelectionChanged);
		saveButton.disableProperty().bind(instance.getSelectionModel().selectedItemProperty().isNull()
				.or(username.textProperty().isEmpty()).or(password.textProperty().isEmpty()));
		loadEntity();
		loadInstances();
	}

	private void loadEntity() {
		if (RepositoryDBConnection.hasConnection()) {
			entity = RepositoryDBConnection.get();
		} else {
			entity = new RepositoryDBConnection();
		}
	}

	private void loadInstances() {
		ConcurrentUtil.run(() -> {
			if (Credential.hasCredential()) {
				final List<RDSInstance> instances = rdsService.listInstances();
				instance.getItems().setAll(instances);
				if (entity != null) {
					final Optional<RDSInstance> optional = instances.stream()
							.filter(i -> Objects.equals(i.getInstanceName(), entity.getInstanceName())).findFirst();
					if (optional.isPresent()) {
						Platform.runLater(() -> instance.getSelectionModel().select(optional.get()));
					}
				}
			}
		});
	}

	@FXML
	private void onSaveButtonClick(final ActionEvent event) {
		try {
			entity.setInstanceName(instance.getValue().getInstanceName());
			entity.setEndpoint(endpoint.getText());
			entity.setPort(Integer.parseInt(port.getText()));
			entity.setUsername(username.getText());
			entity.setPassword(password.getText());
			settingService.saveRepositoryDBConnection(entity);
		} catch (final Exception e) {
			DialogUtil.showErrorDialog(e);
		}
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
			if (RepositoryDBConnection.hasConnection()) {
				final RepositoryDBConnection entity = RepositoryDBConnection.get();
				username.setText(entity.getUsername());
				password.setText(entity.getPassword());
			} else {
				username.textProperty().bind(newValue.usernameProperty());
			}
		}
	}
}
