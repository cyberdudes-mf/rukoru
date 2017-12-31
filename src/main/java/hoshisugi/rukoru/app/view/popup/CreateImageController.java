package hoshisugi.rukoru.app.view.popup;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.CreateMachineImageRequest;
import hoshisugi.rukoru.app.models.EC2Instance;
import hoshisugi.rukoru.app.models.Tag;
import hoshisugi.rukoru.app.services.auth.AuthService;
import hoshisugi.rukoru.app.services.ec2.EC2Service;
import hoshisugi.rukoru.flamework.annotations.FXController;
import hoshisugi.rukoru.flamework.controls.BaseController;
import hoshisugi.rukoru.flamework.util.ConcurrentUtil;
import hoshisugi.rukoru.flamework.util.DialogUtil;
import hoshisugi.rukoru.flamework.util.FXUtil;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

@FXController(title = "イメージ作成")
public class CreateImageController extends BaseController {

	@FXML
	private TextField name;

	@FXML
	private TextField description;

	@FXML
	private CheckBox noReboot;

	@Inject
	private AuthService authService;

	@Inject
	private EC2Service ec2Service;

	private final ObjectProperty<EC2Instance> target = new SimpleObjectProperty<>(this, "target");

	public ObjectProperty<EC2Instance> targetProperty() {
		return target;
	}

	@Override
	public void initialize(final URL url, final ResourceBundle resource) {
		target.addListener((observable, oldValue, newValue) -> {
			name.setText(newValue.getName());
		});
	}

	@FXML
	private void onOKButtonClick(final ActionEvent event) {

		ConcurrentUtil.run(() -> {
			final CreateMachineImageRequest request = new CreateMachineImageRequest();
			request.setInstanceId(target.get().getInstanceId());
			request.setName(name.getText());
			request.setDescription(name.getText());
			request.setNoReboot(noReboot.isSelected());
			request.getTags().add(new Tag("SpiderInstance", ""));

			ec2Service.createMachineImage(authService.load().get(), request);

			Platform.runLater(() -> {
				DialogUtil.showInfoDialog("インスタンス作成", String.format("[%s] のイメージ作成を受け付けました。", request.getName()));
				FXUtil.getStage(event).close();
			});
		});
	}

	@FXML
	private void onCancelButtonClick(final ActionEvent event) {
		FXUtil.getStage(event).close();
	}
}
