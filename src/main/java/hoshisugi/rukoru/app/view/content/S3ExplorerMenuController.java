package hoshisugi.rukoru.app.view.content;

import java.net.URL;
import java.util.ResourceBundle;

import hoshisugi.rukoru.flamework.controls.BaseController;
import hoshisugi.rukoru.flamework.util.AssetUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;;

public class S3ExplorerMenuController extends BaseController {

	@FXML
	private Button backButton;

	@FXML
	private Button nextButton;

	@FXML
	private Button refreshButton;

	@FXML
	private Button homeButton;

	@FXML
	private TextField pathField;

	@Override
	public void initialize(final URL url, final ResourceBundle resource) {
		backButton.setGraphic(new ImageView(AssetUtil.getImage("back_16x16.png")));
		nextButton.setGraphic(new ImageView(AssetUtil.getImage("next_16x16.png")));
		refreshButton.setGraphic(new ImageView(AssetUtil.getImage("refresh_16x16.png")));
		homeButton.setGraphic(new ImageView(AssetUtil.getImage("home_16x16.png")));
	}

}
