package hoshisugi.rukoru.app.view.content;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.AMI;
import hoshisugi.rukoru.app.models.AuthSetting;
import hoshisugi.rukoru.app.services.auth.AuthService;
import hoshisugi.rukoru.app.services.ec2.EC2Service;
import hoshisugi.rukoru.flamework.controls.BaseController;
import hoshisugi.rukoru.flamework.util.ConcurrentUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class AMITabController extends BaseController {

	@FXML
	private TableView<AMI> tableView;

	@FXML
	private TableColumn<AMI, String> nameColumn;

	@FXML
	private TableColumn<AMI, String> creationDateColumn;

	@FXML
	private TableColumn<AMI, String> operationColumn;

	@Inject
	private AuthService authService;

	@Inject
	private EC2Service ec2Service;

	@Override
	public void initialize(final URL arg0, final ResourceBundle arg1) {
		ConcurrentUtil.run(this::loadImages);
	}

	private void loadImages() {
		final Optional<AuthSetting> optional = authService.load();
		if (optional.isPresent()) {
			final AuthSetting authSetting = authService.load().get();
			final List<AMI> images = ec2Service.listImages(authSetting);
			Platform.runLater(() -> {
				tableView.getItems().addAll(FXCollections.observableArrayList(images));
			});
		}
	}
}
