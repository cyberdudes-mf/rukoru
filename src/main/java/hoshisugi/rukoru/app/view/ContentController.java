package hoshisugi.rukoru.app.view;

import static hoshisugi.rukoru.app.enums.Preferences.ImageUrl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import com.google.common.base.Strings;
import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.settings.Preference;
import hoshisugi.rukoru.app.services.settings.LocalSettingService;
import hoshisugi.rukoru.app.view.ds.DSContentController;
import hoshisugi.rukoru.app.view.ec2.EC2ContentController;
import hoshisugi.rukoru.app.view.redmine.TaskboardController;
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

	private final Map<Class<? extends BaseController>, Parent> contents = new HashMap<>();

	@FXML
	private AnchorPane layoutRoot;

	@Inject
	private LocalSettingService service;

	private ImageView topImage;

	@Override
	public void initialize(final URL url, final ResourceBundle resource) {
		showTopImage();
		ConcurrentUtil.run(() -> {
			loadContent(DSContentController.class);
			loadContent(EC2ContentController.class);
			loadContent(RepositoryDBContentController.class);
			loadContent(S3ExplorerController.class);
			loadContent(TaskboardController.class);
		});
	}

	public void showContent(final Class<? extends BaseController> controller) {
		setContent(contents.get(controller));
	}

	public void showHome() {
		setContent(topImage);
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
			topImage = new ImageView(getImageUrl());
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

	private String getImageUrl() {
		try {
			final Optional<Preference> preference = service.findPreference(ImageUrl);
			return preference.map(Preference::getValue).filter(v -> !Strings.isNullOrEmpty(v))
					.orElse(ImageUrl.defaultValue());
		} catch (final Exception e) {
			return ImageUrl.defaultValue();
		}
	}

	public void refreshTopImage() {
		final Image image = new Image(getImageUrl());
		Platform.runLater(() -> topImage.setImage(image));
	}
}