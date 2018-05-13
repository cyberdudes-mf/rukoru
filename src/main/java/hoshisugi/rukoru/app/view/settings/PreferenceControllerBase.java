package hoshisugi.rukoru.app.view.settings;

import hoshisugi.rukoru.framework.base.BaseController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public abstract class PreferenceControllerBase extends BaseController implements PreferenceContent {

	@FXML
	protected void onApplyButtonClick(final ActionEvent event) {
		apply();
	}

}
