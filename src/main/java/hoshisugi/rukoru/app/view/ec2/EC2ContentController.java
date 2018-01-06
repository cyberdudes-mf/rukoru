package hoshisugi.rukoru.app.view.ec2;

import java.net.URL;
import java.util.ResourceBundle;

import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.util.AssetUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;

public class EC2ContentController extends BaseController {

	@FXML
	private TabPane tabPane;

	@FXML
	private Tab instanceTab;

	@FXML
	private Tab amiTab;

	@Override
	public void initialize(final URL url, final ResourceBundle resource) {
		instanceTab.setGraphic(new ImageView(AssetUtil.getImage("16x16/EC2_Instance.png")));
		amiTab.setGraphic(new ImageView(AssetUtil.getImage("16x16/EC2_AMI.png")));
	}

}
