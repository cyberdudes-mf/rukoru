package hoshisugi.rukoru.app.view.ec2;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.auth.AuthSetting;
import hoshisugi.rukoru.app.models.ec2.CreateMachineImageRequest;
import hoshisugi.rukoru.app.models.ec2.EC2Instance;
import hoshisugi.rukoru.app.models.ec2.MachineImage;
import hoshisugi.rukoru.app.models.ec2.Tag;
import hoshisugi.rukoru.app.services.ec2.EC2Service;
import hoshisugi.rukoru.framework.annotations.FXController;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import hoshisugi.rukoru.framework.util.DialogUtil;
import hoshisugi.rukoru.framework.util.FXUtil;
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
	private ImageTabController imageController;

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
			if (!AuthSetting.hasSetting()) {
				DialogUtil.showWarningDialog("警告", "認証情報を設定してください。\n[メニュー] - [Settings] - [認証設定]");
				return;
			}

			final CreateMachineImageRequest request = createRequest();
			final List<MachineImage> images = ec2Service.createMachineImage(request);
			imageController.getItems().addAll(0, images);

			Platform.runLater(() -> {
				DialogUtil.showInfoDialog("インスタンス作成", String.format("[%s] のイメージ作成を受け付けました。", request.getName()));
				FXUtil.getStage(event).close();
			});
		});
	}

	private CreateMachineImageRequest createRequest() {
		final CreateMachineImageRequest request = new CreateMachineImageRequest();
		request.setInstanceId(target.get().getInstanceId());
		request.setName(name.getText());
		request.setDescription(name.getText());
		request.setNoReboot(noReboot.isSelected());
		request.getTags().add(new Tag("SpiderInstance", ""));
		return request;
	}

	@FXML
	private void onCancelButtonClick(final ActionEvent event) {
		FXUtil.getStage(event).close();
	}
}
