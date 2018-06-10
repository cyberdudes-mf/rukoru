package hoshisugi.rukoru.app.view.scrum;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.scrum.ToolButton;
import hoshisugi.rukoru.app.services.settings.LocalSettingService;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.util.AssetUtil;
import hoshisugi.rukoru.framework.util.BrowserUtil;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;

public class ScrumController extends BaseController {

	@FXML
	private LineChart<Number, Number> burnDownChart;

	@FXML
	private Button factorsButton;

	@FXML
	private HBox toolBox;

	@Inject
	private LocalSettingService settingService;

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		factorsButton.setGraphic(new ImageView(AssetUtil.getImage("24x24/gear_add.png")));
		createToolButtons();

		final XYChart.Series<Number, Number> ideal = new XYChart.Series<>();
		ideal.setName("理想線");
		ideal.getData().add(new XYChart.Data<>(0, 102.5));
		ideal.getData().add(new XYChart.Data<>(1, 89.5));
		ideal.getData().add(new XYChart.Data<>(2, 76.5));
		ideal.getData().add(new XYChart.Data<>(3, 63.5));
		ideal.getData().add(new XYChart.Data<>(4, 50.5));
		ideal.getData().add(new XYChart.Data<>(5, 37.5));
		ideal.getData().add(new XYChart.Data<>(6, 24.5));
		ideal.getData().add(new XYChart.Data<>(7, 11.5));
		ideal.getData().add(new XYChart.Data<>(8, 0));
		burnDownChart.getData().add(ideal);

		final XYChart.Series<Number, Number> actual = new XYChart.Series<>();
		actual.setName("実績線");
		actual.getData().add(new XYChart.Data<>(0, 102.5));
		actual.getData().add(new XYChart.Data<>(1, 92.5));
		actual.getData().add(new XYChart.Data<>(2, 79.5));
		actual.getData().add(new XYChart.Data<>(3, 66.5));
		actual.getData().add(new XYChart.Data<>(4, 47.5));
		// actual.getData().add(new XYChart.Data<>(5, 102.5));
		// actual.getData().add(new XYChart.Data<>(6, 102.5));
		// actual.getData().add(new XYChart.Data<>(7, 102.5));
		// actual.getData().add(new XYChart.Data<>(8, 102.5));
		burnDownChart.getData().add(actual);

		final XYChart.Series<Number, Number> velocity = new XYChart.Series<>();
		velocity.setName("見込み線");
		velocity.getData().add(new XYChart.Data<>(4, 47.5));
		velocity.getData().add(new XYChart.Data<>(5, 32.5));
		velocity.getData().add(new XYChart.Data<>(6, 17.5));
		velocity.getData().add(new XYChart.Data<>(7, 2.5));
		velocity.getData().add(new XYChart.Data<>(8, 0));
		burnDownChart.getData().add(velocity);
	}

	private void createToolButtons() {
		ConcurrentUtil.run(() -> {
			final List<ToolButton> toolButtons = settingService.getToolButtons();
			toolButtons.stream().forEach(t -> {
				final Button button = new Button(t.getLabel());
				final Rectangle shape = new Rectangle(12, 12, t.getColor());
				button.setGraphic(shape);
				button.prefHeightProperty().bind(factorsButton.heightProperty());
				button.setUserData(t);
				button.setOnAction(this::onToolButtonClick);
				Platform.runLater(() -> toolBox.getChildren().add(button));
			});
		});
	}

	@FXML
	private void onFactorsButtonClick(final ActionEvent event) {

	}

	public void refreshToolButtons() {
		Platform.runLater(() -> {
			toolBox.getChildren().clear();
			createToolButtons();
		});
	}

	private void onToolButtonClick(final ActionEvent event) {
		final Button button = (Button) event.getSource();
		final ToolButton toolButton = (ToolButton) button.getUserData();
		BrowserUtil.browse(toolButton.getUrl().toString());
	}
}
