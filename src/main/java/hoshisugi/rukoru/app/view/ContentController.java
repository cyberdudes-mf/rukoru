package hoshisugi.rukoru.app.view;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import hoshisugi.rukoru.app.view.content.EC2ContentController;
import hoshisugi.rukoru.app.view.content.RepositoryDBContentController;
import hoshisugi.rukoru.app.view.content.S3ContentController;
import hoshisugi.rukoru.flamework.controls.BaseController;
import hoshisugi.rukoru.flamework.util.ConcurrentUtil;
import hoshisugi.rukoru.flamework.util.FXUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

public class ContentController extends BaseController {

	private final Map<Class<? extends BaseController>, Parent> contents = new HashMap<>();

	@FXML
	private AnchorPane layoutRoot;

	@Override
	public void initialize(final URL url, final ResourceBundle resource) {
		ConcurrentUtil.run(() -> {
			loadContent(EC2ContentController.class);
			loadContent(RepositoryDBContentController.class);
			loadContent(S3ContentController.class);
		});
	}

	public void showContent(final Class<? extends BaseController> controller) {
		layoutRoot.getChildren().clear();
		final Parent parent = contents.get(controller);
		layoutRoot.getChildren().add(parent);
		AnchorPane.setTopAnchor(parent, 0.0);
		AnchorPane.setLeftAnchor(parent, 0.0);
		AnchorPane.setRightAnchor(parent, 0.0);
		AnchorPane.setBottomAnchor(parent, 0.0);
	}

	private void loadContent(final Class<? extends BaseController> controller) {
		try {
			final Parent parent = FXMLLoader.load(FXUtil.getURL(controller));
			contents.put(controller, parent);
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
