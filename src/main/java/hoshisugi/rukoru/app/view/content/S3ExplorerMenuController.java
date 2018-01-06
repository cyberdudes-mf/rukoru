package hoshisugi.rukoru.app.view.content;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.S3Item;
import hoshisugi.rukoru.framework.controls.BaseController;
import hoshisugi.rukoru.framework.util.AssetUtil;
import hoshisugi.rukoru.framework.util.DialogUtil;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
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
	private Button upButton;

	@FXML
	private TextField pathField;

	@Inject
	private S3ExplorerController explorer;

	@Override
	public void initialize(final URL url, final ResourceBundle resource) {
		backButton.setGraphic(new ImageView(AssetUtil.getImage("16x16/back.png")));
		nextButton.setGraphic(new ImageView(AssetUtil.getImage("16x16/next.png")));
		refreshButton.setGraphic(new ImageView(AssetUtil.getImage("16x16/refresh.png")));
		homeButton.setGraphic(new ImageView(AssetUtil.getImage("16x16/home.png")));
		upButton.setGraphic(new ImageView(AssetUtil.getImage("16x16/up.png")));
		explorer.selectedItemProperty().addListener(this::selectedItemChanged);
	}

	private void selectedItemChanged(final ObservableValue<? extends S3Item> observable, final S3Item oldValue,
			final S3Item newValue) {
		if (newValue != null) {
			pathField.setText(newValue.getPath());
		} else {
			pathField.setText(null);
		}
	}

	@FXML
	private void onBackButtonClick(final ActionEvent event) {
		DialogUtil.showWarningDialog("警告", "Not yet implemented.");
	}

	@FXML
	private void onNextButtonClick(final ActionEvent event) {
		DialogUtil.showWarningDialog("警告", "Not yet implemented.");
	}

	@FXML
	private void onRefreshButtonClick(final ActionEvent event) {
		final S3Item selectedItem = explorer.getSelectedItem();
		explorer.setSelectedItem(null);
		explorer.setSelectedItem(selectedItem);
	}

	@FXML
	private void onHomeButtonClick(final ActionEvent event) {
		explorer.setSelectedItem(explorer.getRootItem());
	}

	@FXML
	private void onUpButtonClick(final ActionEvent event) {
		final S3Item selectedItem = explorer.getSelectedItem();
		final S3Item parent = selectedItem.getParent();
		if (parent != null) {
			explorer.setSelectedItem(parent);
		}
	}

}
