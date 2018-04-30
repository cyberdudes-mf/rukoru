package hoshisugi.rukoru;

import hoshisugi.rukoru.app.inject.RukoruModuleConfigurator;
import hoshisugi.rukoru.app.view.MainController;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.inject.Injector;
import hoshisugi.rukoru.framework.util.AssetUtil;
import hoshisugi.rukoru.framework.util.FXUtil;
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
			FXUtil.setPrimaryStage(primaryStage);
			Injector.init(new RukoruModuleConfigurator());
			final Class<? extends BaseController> controllerClass = MainController.class;
			final Parent root = (BorderPane) FXMLLoader.load(FXUtil.getURL(controllerClass));
			final Scene scene = new Scene(root, 1080, 600);
			primaryStage.setScene(scene);
			primaryStage.setTitle(FXUtil.getTitle(controllerClass));
			primaryStage.getIcons().add(AssetUtil.getImage("32x32/icon.png"));
			primaryStage.show();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(final String[] args) {
		launch(args);
	}

}
