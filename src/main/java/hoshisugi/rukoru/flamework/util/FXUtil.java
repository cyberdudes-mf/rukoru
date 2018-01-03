package hoshisugi.rukoru.flamework.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.function.Function;

import com.google.common.base.Strings;

import hoshisugi.rukoru.flamework.annotations.FXController;
import hoshisugi.rukoru.flamework.controls.BaseController;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class FXUtil {

	private static Stage primaryStage;

	public static URL getURL(final Class<? extends BaseController> controller) throws FileNotFoundException {
		String viewName = getViewName(controller);
		if (viewName == null) {
			viewName = getFXMLName(controller);
		}
		final URL url = controller.getResource(viewName);
		if (url == null) {
			throw new FileNotFoundException(viewName);
		}
		return url;
	}

	public static Stage getStage(final Event event) {
		final Object source = event.getSource();
		if (source instanceof Node) {
			return getStage((Node) source);
		} else if (source instanceof MenuItem) {
			final MenuItem item = (MenuItem) source;
			return (Stage) item.getParentPopup().getOwnerWindow();
		}
		return null;
	}

	public static Stage getStage(final Node node) {
		if (node == null) {
			return null;
		}
		final Scene scene = node.getScene();
		if (scene == null) {
			return null;
		}
		return (Stage) scene.getWindow();
	}

	public static <C extends BaseController> C popup(final Class<C> controller, final Window owner) {
		try {
			final FXMLLoader fxmlLoader = new FXMLLoader(getURL(controller));
			final Parent parent = fxmlLoader.load();
			final Scene scene = new Scene(parent);
			scene.getStylesheets().addAll(owner.getScene().getStylesheets());
			final Stage stage = new Stage();
			stage.initStyle(StageStyle.UTILITY);
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(owner);
			stage.setScene(scene);
			stage.setTitle(getTitle(controller));
			stage.show();
			return fxmlLoader.getController();
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static String getAnnotatedValue(final Class<? extends BaseController> controller,
			final Function<FXController, String> function) {
		final FXController annotation = controller.getAnnotation(FXController.class);
		if (annotation == null) {
			return null;
		}
		return Strings.emptyToNull(function.apply(annotation));
	}

	private static String getViewName(final Class<? extends BaseController> controller) {
		return getAnnotatedValue(controller, FXController::fxml);
	}

	public static String getTitle(final Class<? extends BaseController> controller) {
		return getAnnotatedValue(controller, FXController::title);
	}

	private static String getFXMLName(final Class<? extends BaseController> controller) {
		final String controllerClassName = controller.getSimpleName();
		final String baseName = controllerClassName.replace("Controller", "View");
		return baseName + ".fxml";
	}

	public static boolean isDoubleClicked(final MouseEvent event) {
		return event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2;
	}

	public static void setPrimaryStage(final Stage stage) {
		primaryStage = stage;
	}

	public static Stage getPrimaryStage() {
		return primaryStage;
	}
}
