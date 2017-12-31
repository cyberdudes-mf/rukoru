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
import hoshisugi.rukoru.app.view.popup.CreateInstanceController;
import hoshisugi.rukoru.flamework.controls.BaseController;
import hoshisugi.rukoru.flamework.controls.GraphicTableCell;
import hoshisugi.rukoru.flamework.util.AssetUtil;
import hoshisugi.rukoru.flamework.util.ConcurrentUtil;
import hoshisugi.rukoru.flamework.util.FXUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;

public class AMITabController extends BaseController {

	@FXML
	private TableView<AMI> tableView;

	@FXML
	private TableColumn<AMI, String> nameColumn;

	@FXML
	private TableColumn<AMI, String> creationDateColumn;

	@FXML
	private TableColumn<AMI, AMI> operationColumn;

	@FXML
	private Button refreshButton;

	@Inject
	private AuthService authService;

	@Inject
	private EC2Service ec2Service;

	private AMI selectedEntity;

	@Override
	public void initialize(final URL url, final ResourceBundle resource) {
		operationColumn.setCellValueFactory(GraphicTableCell.forTableCellValueFactory());
		operationColumn.setCellFactory(GraphicTableCell.forTableCellFactory(this::createOperationButton));
		refreshButton.setGraphic(new ImageView(AssetUtil.getImage("refresh_24x24.png")));
		ConcurrentUtil.run(this::loadImages);
	}

	@FXML
	private void onRefreshButtonClick(final ActionEvent event) {
		ConcurrentUtil.run(this::loadImages);
	}

	private void loadImages() {
		try {
			Platform.runLater(() -> refreshButton.setDisable(true));
			tableView.getItems().clear();
			final Optional<AuthSetting> optional = authService.load();
			if (optional.isPresent()) {
				final AuthSetting authSetting = optional.get();
				final List<AMI> images = ec2Service.listImages(authSetting);
				Platform.runLater(() -> tableView.getItems().addAll(FXCollections.observableArrayList(images)));
			}
		} finally {
			Platform.runLater(() -> refreshButton.setDisable(false));
		}
	}

	private Button createOperationButton(final AMI entity) {
		final Button button = new Button("作成");
		button.setGraphic(new ImageView(AssetUtil.getImage("add_16x16.png")));
		button.setOnAction(this::onCreateButtonClick);
		button.setUserData(entity);
		return button;
	}

	private void onCreateButtonClick(final ActionEvent event) {
		final Button button = (Button) event.getSource();
		selectedEntity = (AMI) button.getUserData();
		FXUtil.popup(CreateInstanceController.class, FXUtil.getStage(event));
	}

	public AMI getSelectedEntity() {
		return selectedEntity;
	}

}
