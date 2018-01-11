package hoshisugi.rukoru.app.view;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import hoshisugi.rukoru.app.view.ec2.EC2ContentController;
import hoshisugi.rukoru.app.view.repositorydb.RepositoryDBContentController;
import hoshisugi.rukoru.app.view.s3.S3ExplorerController;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.inject.Injector;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import hoshisugi.rukoru.framework.util.FXUtil;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;

public class ContentController extends BaseController {

	private final Map<Class<? extends BaseController>, Parent> contents = new HashMap<>();

	@FXML
	private AnchorPane layoutRoot;

	@Override
	public void initialize(final URL url, final ResourceBundle resource) {
		showTopImage();
		ConcurrentUtil.run(() -> {
			loadContent(EC2ContentController.class);
			loadContent(RepositoryDBContentController.class);
			loadContent(S3ExplorerController.class);
		});
	}

	public void showContent(final Class<? extends BaseController> controller) {
		layoutRoot.getChildren().clear();
		final Parent content = contents.get(controller);
		layoutRoot.getChildren().add(content);
		AnchorPane.setTopAnchor(content, 0.0);
		AnchorPane.setLeftAnchor(content, 0.0);
		AnchorPane.setRightAnchor(content, 0.0);
		AnchorPane.setBottomAnchor(content, 0.0);
	}

	private void loadContent(final Class<? extends BaseController> controller) {
		try {
			final Parent parent = FXMLLoader.load(FXUtil.getURL(controller));
			contents.put(controller, parent);
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private void showTopImage() {
		ConcurrentUtil.run(() -> {
			final ImageView topImage = new ImageView(new Image(
					"https://s3-ap-northeast-1.amazonaws.com/com.appresso.dsc.redmine/assets/rukoru/top.jpg"));
			topImage.setPreserveRatio(true);
			final BorderPane parent = (BorderPane) layoutRoot.getParent();
			final Optional<ReadOnlyDoubleProperty> left = Optional.ofNullable(parent.getLeft()).map(Region.class::cast)
					.map(Region::widthProperty);
			final Optional<ReadOnlyDoubleProperty> right = Optional.ofNullable(parent.getRight())
					.map(Region.class::cast).map(Region::widthProperty);
			final DoubleProperty defaultValue = new SimpleDoubleProperty(0d);
			final DoubleBinding fitWidth = parent.widthProperty().subtract(10d).subtract(left.orElse(defaultValue))
					.subtract(right.orElse(defaultValue));
			topImage.fitWidthProperty().bind(fitWidth);
			final ToolBarController toolBar = Injector.getInstance(ToolBarController.class);
			if (!toolBar.isSelected()) {
				Platform.runLater(() -> layoutRoot.getChildren().add(topImage));
			}
		});
	}

}
