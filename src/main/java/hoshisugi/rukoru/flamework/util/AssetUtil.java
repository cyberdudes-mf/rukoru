package hoshisugi.rukoru.flamework.util;

import java.io.FileNotFoundException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javafx.scene.image.Image;

public class AssetUtil {

	public static URL getURL(final String directory, final String resourceName) throws FileNotFoundException {
		final URL url = AssetUtil.class.getClassLoader().getResource(directory + "/" + resourceName);
		if (url == null) {
			throw new FileNotFoundException(resourceName);
		}
		return url;
	}

	public static URI getURI(final String directory, final String resourceName) throws FileNotFoundException {
		try {
			return getURL(directory, resourceName).toURI();
		} catch (final URISyntaxException e) {
			throw new FileNotFoundException(directory + System.getProperty("path.separator") + resourceName);
		}
	}

	public static String getAssetPath(final String resourceName) {
		try {
			return getURL("assets", resourceName).toExternalForm();
		} catch (final FileNotFoundException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static URI getDataURI(final String resourceName) throws FileNotFoundException {
		return getURI("assets/data", resourceName);
	}

	public static Image getImage(final String img, final double width, final double height) {
		return new Image(getAssetPath(img), width, height, false, false);
	}

	public static Image getImage(final String img) {
		return new Image(getAssetPath(img));
	}

	public static URI getParseConfigURI(final String resourceName) {
		try {
			return getURI("parse/config", resourceName);
		} catch (final FileNotFoundException e) {
			throw new UncheckedIOException(e);
		}
	}

}
