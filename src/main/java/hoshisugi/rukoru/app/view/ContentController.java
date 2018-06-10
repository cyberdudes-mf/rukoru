package hoshisugi.rukoru.app.view;

import static hoshisugi.rukoru.app.enums.Preferences.ImageUrl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import com.google.common.base.Strings;
import com.google.inject.Inject;

import hoshisugi.rukoru.app.enums.RukoruModule;
import hoshisugi.rukoru.app.models.settings.Preference;
import hoshisugi.rukoru.app.services.settings.LocalSettingService;
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

	private final Map<RukoruModule, Parent> contents = new HashMap<>();

	@FXML
	private AnchorPane layoutRoot;

	@Inject
	private LocalSettingService service;

	private ImageView topImage;

	@Override
	public void initialize(final URL url, final ResourceBundle resource) {
		showTopImage();

		ConcurrentUtil.run(() -> {
			final Map<String, Preference> preferences = service.getPreferencesByCategory("Module");
			loadContents(preferences);
		});
	}

	public void showContent(final RukoruModule rukoruModule) {
		setContent(contents.get(rukoruModule));
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

	public void loadContents(final Map<String, Preference> preferences) {
		Stream.of(RukoruModule.values()).filter(m -> m.getControllerClass() != null)
				.filter(m -> isActive(m, preferences)).forEach(this::loadContent);
	}

	private void loadContent(final RukoruModule rukoruModule) {
		if (contents.containsKey(rukoruModule)) {
			return;
		}
		try {
			final Parent parent = FXMLLoader.load(FXUtil.getURL(rukoruModule.getControllerClass()));
			contents.put(rukoruModule, parent);
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private boolean isActive(final RukoruModule rukoruModule, final Map<String, Preference> preferences) {
		final Preference preference = preferences.get(rukoruModule.toString());
		return preference == null || Boolean.parseBoolean(preference.getValue());
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
			Platform.runLater(() -> {
				if (!toolBar.isSelected()) {
					layoutRoot.getChildren().add(topImage);
				}
			});
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