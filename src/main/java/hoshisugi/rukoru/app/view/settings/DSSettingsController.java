package hoshisugi.rukoru.app.view.settings;

import java.net.URL;
import java.util.ResourceBundle;

import hoshisugi.rukoru.app.models.settings.DSSetting;
import hoshisugi.rukoru.framework.annotations.FXController;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.util.FXUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

@FXController(title = "DataSpider")
public class DSSettingsController extends BaseController implements PreferenceContent {

	@FXML
	private Button createButton;

	@FXML
	private Button deleteButton;

	@FXML
	private TableView<DSSetting> tableView;

	@FXML
	private TableColumn<DSSetting, String> nameColumn;

	@FXML
	private TableColumn<DSSetting, String> dsHomeColumn;

	@FXML
	private TableColumn<DSSetting, String> executionTypeColumn;

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {

	}

	@Override
	public void apply() {

	}

	@FXML
	private void onEntryButtonClick(final ActionEvent event) {
		FXUtil.popup(EntryDSController.class, FXUtil.getStage(event));
	}

	@FXML
	private void onDeleteButtonClick(final ActionEvent event) {

	}

	@FXML
	private void onApplyButtonClick(final ActionEvent e) {
		apply();
	}
}
