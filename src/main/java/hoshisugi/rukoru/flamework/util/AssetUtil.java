package hoshisugi.rukoru.flamework.util;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.scene.image.Image;

public class AssetUtil {

	public static URL getURL(final String directory, final String resourceName) {
		final URL url = AssetUtil.class.getClassLoader().getResource(directory + "/" + resourceName);
		if (url == null) {
			throw new UncheckedIOException(new FileNotFoundException(resourceName));
		}
		return url;
	}

	public static URI getURI(final String directory, final String resourceName) {
		try {
			return getURL(directory, resourceName).toURI();
		} catch (final URISyntaxException e) {
			throw new UncheckedIOException(
					new FileNotFoundException(directory + System.getProperty("path.separator") + resourceName));
		}
	}

	public static String getAssetPath(final String resourceName) {
		return getURL("assets", resourceName).toExternalForm();
	}

	public static Image getImage(final String img, final double width, final double height) {
		return new Image(getAssetPath(img), width, height, false, false);
	}

	public static Image getImage(final String img) {
		return new Image(getAssetPath(img));
	}

	public static String loadSQL(final String fileName) {
		final URI uri = AssetUtil.getURI("sql", fileName);
		try {
			return new String(Files.readAllBytes(Paths.get(uri)), UTF_8.toString());
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
