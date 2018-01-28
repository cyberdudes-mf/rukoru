package hoshisugi.rukoru.app.view;

import static hoshisugi.rukoru.app.enums.Preferences.Home.ImageUrl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.settings.Preference;
import hoshisugi.rukoru.app.services.settings.LocalSettingService;
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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;

public class ContentController extends BaseController {

	private static final String DEFAULT_IMAGE_URL = "https://s3-ap-northeast-1.amazonaws.com/com.appresso.dsc.rukoru/assets/top.jpg";

	private final Map<Class<? extends BaseController>, Parent> contents = new HashMap<>();

	@FXML
	private AnchorPane layoutRoot;

	@Inject
	private LocalSettingService service;

	private Node homeContent;

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
		setContent(contents.get(controller));
	}

	public void showHome() {
		setContent(homeContent);
	}

	private void setContent(final Node node) {
		layoutRoot.getChildren().clear();
		layoutRoot.getChildren().add(node);
		AnchorPane.setTopAnchor(node, 0.0);
		AnchorPane.setLeftAnchor(node, 0.0);
		AnchorPane.setRightAnchor(node, 0.0);
		AnchorPane.setBottomAnchor(node, 0.0);
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
			final Optional<Preference> preference = service.findPreferenceByCategoryAndKey(ImageUrl.getCategory(),
					ImageUrl.getKey());
			final String imageUrl = preference.isPresent() ? preference.get().getValue() : DEFAULT_IMAGE_URL;
			final ImageView topImage = new ImageView(new Image(imageUrl));
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
			homeContent = topImage;
			final ToolBarController toolBar = Injector.getInstance(ToolBarController.class);
			if (!toolBar.isSelected()) {
				Platform.runLater(() -> layoutRoot.getChildren().add(homeContent));
			}
		});
	}

}
