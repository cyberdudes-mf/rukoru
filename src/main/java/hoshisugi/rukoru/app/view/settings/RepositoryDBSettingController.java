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
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

@FXController(title = "RepositoryDB")
public class RepositoryDBSettingController extends BaseController implements PreferenceContent {

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

	@FXML
	private void onApplyButtonClick(final ActionEvent event) {
		apply();
	}

	@Override
	public void apply() {
		ConcurrentUtil.run(() -> {
			entity.setInstanceName(instance.getValue().getInstanceName());
			entity.setEndpoint(endpoint.getText());
			entity.setPort(Integer.parseInt(port.getText()));
			entity.setUsername(username.getText());
			entity.setPassword(password.getText());
			settingService.saveRepositoryDBConnection(entity);
		});
	}
}
