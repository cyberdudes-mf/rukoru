package hoshisugi.rukoru.app.view.settings;

import java.net.URL;
import java.util.ResourceBundle;

import hoshisugi.rukoru.app.enums.ExecutionType;
import hoshisugi.rukoru.app.models.ec2.MachineImage;
import hoshisugi.rukoru.framework.annotations.FXController;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.controls.PropertyListCell;
import hoshisugi.rukoru.framework.util.AssetUtil;
import hoshisugi.rukoru.framework.util.FXUtil;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

@FXController(title = "DataSpider登録")
public class EntryDSController extends BaseController {
	@FXML
	private TextField name;

	@FXML
	private TextField dsHome;

	@FXML
	private ComboBox<ExecutionType> executionType;

	@FXML
	private Button directoryChooser;

	@FXML
	private Button okButton;

	private final ObjectProperty<MachineImage> target = new SimpleObjectProperty<>(this, "target");

	public ObjectProperty<MachineImage> targetProperty() {
		return target;
	}

	@Override
	public void initialize(final URL url, final ResourceBundle resource) {
		target.addListener((observable, oldValue, newValue) -> {
			name.setText(newValue.getName());
		});
		executionType.getItems().addAll(ExecutionType.values());
		executionType.setCellFactory(PropertyListCell.forListView(ExecutionType::getDisplayName));
		executionType.setButtonCell(executionType.getCellFactory().call(null));
		executionType.getSelectionModel().select(ExecutionType.Service);
		directoryChooser.setGraphic(new ImageView(AssetUtil.getImage("16x16/navigate_plus.png")));
		okButton.disableProperty().bind(name.textProperty().isEmpty().or(dsHome.textProperty().isEmpty()));
	}

	@FXML
	private void onOKButtonClick(final ActionEvent event) {

	}

	@FXML
	private void onCancelButtonClick(final ActionEvent event) {
		close(FXUtil.getStage(event));
	}

	private void close(final Stage stage) {
		stage.close();
	}
}
