package hoshisugi.rukoru.app.view.settings;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import hoshisugi.rukoru.app.enums.DSSettingState;
import hoshisugi.rukoru.app.enums.ExecutionType;
import hoshisugi.rukoru.app.models.settings.DSSetting;
import hoshisugi.rukoru.framework.annotations.FXController;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.controls.PropertyListCell;
import hoshisugi.rukoru.framework.util.AssetUtil;
import hoshisugi.rukoru.framework.util.FXUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;

@FXController(title = "登録")
public class EntryDSController extends BaseController {
	@FXML
	private TextField name;

	@FXML
	private TextField executionPath;

	@FXML
	private ComboBox<ExecutionType> executionType;

	@FXML
	private Button directoryChooserButton;

	@FXML
	private Button okButton;

	@Override
	public void initialize(final URL url, final ResourceBundle resource) {
		executionType.getItems().addAll(ExecutionType.values());
		executionType.setCellFactory(PropertyListCell.forListView(ExecutionType::toString));
		executionType.setButtonCell(executionType.getCellFactory().call(null));
		executionType.getSelectionModel().select(ExecutionType.Service);
		directoryChooserButton.setGraphic(new ImageView(AssetUtil.getImage("16x16/folder.png")));
		okButton.disableProperty().bind(name.textProperty().isEmpty().or(executionPath.textProperty().isEmpty()));
	}

	@FXML
	private void onDirectoryChooserButtonClick(final ActionEvent event) {
		final File file = new DirectoryChooser().showDialog(FXUtil.getStage(event));
		if (file != null) {
			executionPath.setText(file.getAbsolutePath());
		}
	}

	public void setOnOkButtonClick(final Consumer<DSSetting> consumer) {
		okButton.setOnAction(e -> {
			final DSSetting setting = new DSSetting();
			setting.setName(name.getText());
			setting.setExecutionPath(executionPath.getText());
			setting.setExecutionType(executionType.getSelectionModel().getSelectedItem().toString());
			setting.setState(DSSettingState.Insert);
			consumer.accept(setting);
			FXUtil.getStage(e).close();
		});
	}

	@FXML
	private void onCancelButtonClick(final ActionEvent event) {
		FXUtil.getStage(event).close();
	}
}