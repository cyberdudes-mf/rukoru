package hoshisugi.rukoru.app.view.content;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.AuthSetting;
import hoshisugi.rukoru.app.models.EC2Instance;
import hoshisugi.rukoru.app.services.auth.AuthService;
import hoshisugi.rukoru.app.services.ec2.EC2Service;
import hoshisugi.rukoru.flamework.controls.BaseController;
import hoshisugi.rukoru.flamework.util.ConcurrentUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

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
	private TableColumn<EC2Instance, Date> launchTimeColumn;

	@FXML
	private TableColumn<EC2Instance, String> autoStopColumn;

	@Inject
	private AuthService authService;

	@Inject
	private EC2Service ec2Service;

	@Override
	public void initialize(final URL url, final ResourceBundle resource) {
		ConcurrentUtil.run(this::loadInstances);
	}

	private void loadInstances() {
		final Optional<AuthSetting> optional = authService.load();
		if (optional.isPresent()) {
			final AuthSetting authSetting = authService.load().get();
			final List<EC2Instance> instances = ec2Service.listInstances(authSetting);
			Platform.runLater(() -> {
				tableView.getItems().addAll(FXCollections.observableArrayList(instances));
			});
		}
	}
}
