package hoshisugi.rukoru.flamework.util;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

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

	public static Image getImage(final String img) {
		return new Image(getAssetPath(img));
	}

	public static String loadSQL(final String fileName) {
		final URI uri = AssetUtil.getURI("sql", fileName);
		try {
			return new String(Files.readAllBytes(toPath(uri)), UTF_8.toString());
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static Path toPath(final URI uri) throws IOException {
		// REVISIT デバッグ実行と jar でやり方を同じにする
		try {
			return Paths.get(uri);
		} catch (final FileSystemNotFoundException e) {
			final String[] array = uri.toString().split("!");
			return FileSystems.newFileSystem(URI.create(array[0]), Collections.emptyMap()).getPath(array[1]);
		}
	}
}
