package hoshisugi.rukoru.app.view.ds;

import java.net.URL;
import java.util.ResourceBundle;

import hoshisugi.rukoru.app.models.settings.DSSetting;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.util.AssetUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;

public class DSEntryController extends BaseController {

	@FXML
	private Button openHomeButton;

	@FXML
	private Label name;

	@FXML
	private ToggleButton controlServerButton;

	@FXML
	private ToggleButton controlStudioButton;

	@FXML
	private ToggleButton controlAllButton;

	private DSSetting dsSetting;

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		openHomeButton.setGraphic(new ImageView(AssetUtil.getImage("32x32/icon.png")));
		controlServerButton.setGraphic(new ImageView(AssetUtil.getImage("32x32/icon.png")));
		controlStudioButton.setGraphic(new ImageView(AssetUtil.getImage("32x32/icon.png")));
		controlAllButton.setGraphic(new ImageView(AssetUtil.getImage("32x32/icon.png")));
	}

	@FXML
	private void onOpenHomeButtonClick(final ActionEvent event) {

	}

	@FXML
	private void onControlServerButtonClick(final ActionEvent event) {
		System.out.println("onControlServerButtonClick");
	}

	@FXML
	private void onControlStudioButtonClick(final ActionEvent event) {
		System.out.println("onControlStudioButtonClick");
	}

	@FXML
	private void onControlAllButtonClick(final ActionEvent event) {
		System.out.println("onControlAllButtonClick");
	}

	public void loadSetting(final DSSetting dsSetting) {
		name.setText(dsSetting.getName());
		this.dsSetting = dsSetting;
	}

}
