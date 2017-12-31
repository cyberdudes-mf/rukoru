package hoshisugi.rukoru.app.view.content;

import java.net.URL;
import java.util.ResourceBundle;

import hoshisugi.rukoru.flamework.controls.BaseController;
import hoshisugi.rukoru.flamework.util.AssetUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;

public class EC2ContentController extends BaseController {

	@FXML
	private Tab instanceTab;

	@FXML
	private Tab amiTab;

	@Override
	public void initialize(final URL url, final ResourceBundle resource) {
		instanceTab.setGraphic(new ImageView(AssetUtil.getImage("EC2_Instance_16x16.png")));
		amiTab.setGraphic(new ImageView(AssetUtil.getImage("EC2_AMI_16x16.png")));
	}

}
