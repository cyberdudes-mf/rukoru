package hoshisugi.rukoru.app.view.settings;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;

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

	@Inject
	private DSSettingsController controller;

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
		// DirectoryChooser 画面で「×」やらで落とすとなぜかフリーズする。解決策が見つからないのでいったんこれで。
		try {
			executionPath.setText(new DirectoryChooser().showDialog(FXUtil.getStage(event)).getAbsolutePath());
		} catch (final Exception e) {
			executionPath.setText("");
		}
	}

	@FXML
	private void onOKButtonClick(final ActionEvent event) {
		final DSSetting setting = new DSSetting();
		setting.setId(controller.getItems().size() + 1);
		setting.setName(name.getText());
		setting.setExecutionPath(executionPath.getText());
		setting.setExecutionType(executionType.getSelectionModel().getSelectedItem().toString());
		controller.getItems().add(setting);
		FXUtil.getStage(event).close();
	}

	@FXML
	private void onCancelButtonClick(final ActionEvent event) {
		FXUtil.getStage(event).close();
	}
}