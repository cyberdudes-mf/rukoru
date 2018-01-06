package hoshisugi.rukoru.app.view.s3;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.s3.ExplorerSelection;
import hoshisugi.rukoru.app.models.s3.S3Item;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.util.AssetUtil;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;;

public class S3ExplorerMenuController extends BaseController {

	@FXML
	private Button prevButton;

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
		prevButton.setGraphic(new ImageView(AssetUtil.getImage("16x16/back.png")));
		prevButton.disableProperty().bind(Bindings.not(explorer.getSelection().hasPrevious()));
		nextButton.setGraphic(new ImageView(AssetUtil.getImage("16x16/next.png")));
		nextButton.disableProperty().bind(Bindings.not(explorer.getSelection().hasNextProperty()));
		refreshButton.setGraphic(new ImageView(AssetUtil.getImage("16x16/refresh.png")));
		homeButton.setGraphic(new ImageView(AssetUtil.getImage("16x16/home.png")));
		upButton.setGraphic(new ImageView(AssetUtil.getImage("16x16/up.png")));
		explorer.getSelection().selectedItemProperty().addListener(this::selectedItemChanged);
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
	private void onPrevButtonClick(final ActionEvent event) {
		explorer.getSelection().goPrevious();
	}

	@FXML
	private void onNextButtonClick(final ActionEvent event) {
		explorer.getSelection().goNext();
	}

	@FXML
	private void onRefreshButtonClick(final ActionEvent event) {
		explorer.reload(explorer.getSelection().getSelectedItem());
	}

	@FXML
	private void onHomeButtonClick(final ActionEvent event) {
		explorer.getSelection().select(explorer.getRootItem());
	}

	@FXML
	private void onUpButtonClick(final ActionEvent event) {
		final ExplorerSelection selection = explorer.getSelection();
		final S3Item selectedItem = selection.getSelectedItem();
		final S3Item parent = selectedItem.getParent();
		if (parent != null) {
			selection.select(parent);
		}
	}

}
