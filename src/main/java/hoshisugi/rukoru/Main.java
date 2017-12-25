package hoshisugi.rukoru;

import hoshisugi.rukoru.app.MainController;
import hoshisugi.rukoru.flamework.controls.ControllerBase;
import hoshisugi.rukoru.flamework.util.Assets;
import hoshisugi.rukoru.flamework.util.FXUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(final Stage primaryStage) {
		try {
			final Class<? extends ControllerBase> controllerClass = MainController.class;
			final Parent root = (BorderPane) FXMLLoader.load(FXUtil.getURL(controllerClass));
			final Scene scene = new Scene(root, 400, 400);
			scene.getStylesheets().add(Assets.APPLICATION_CSS);
			primaryStage.setScene(scene);
			primaryStage.setTitle(FXUtil.getTitle(controllerClass));
			primaryStage.show();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(final String[] args) {
		launch(args);
	}
}
