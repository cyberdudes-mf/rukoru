package hoshisugi.rukoru.app.view.settings;

import java.net.URL;
import java.util.ResourceBundle;

import hoshisugi.rukoru.app.enums.CSSThemes;
import hoshisugi.rukoru.framework.annotations.FXController;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.controls.PropertyListCell;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

@FXController(title = "CSS")
public class CSSSettingController extends BaseController implements PreferenceContent {

	@FXML
	private ComboBox<CSSThemes> cssThemes;

	@Override
	public void initialize(final URL arg0, final ResourceBundle arg1) {
		cssThemes.getItems().addAll(CSSThemes.values());
		cssThemes.setCellFactory(PropertyListCell.forListView(CSSThemes::toString));
		cssThemes.getSelectionModel().select(CSSThemes.of("modena"));
	}

	@FXML
	private void onApplyButtonClick() {

	}

	@Override
	public void apply() {
		// TODO Auto-generated method stub

	}

}
