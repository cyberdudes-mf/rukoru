package hoshisugi.rukoru.framework.util;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
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
		return loadFile("sql", fileName);
	}

	public static String loadJS(final String fileName) {
		return loadFile("js", fileName);
	}

	public static String loadJS(final String fileName, final Object... params) {
		return String.format(loadJS(fileName), params);
	}

	private static String loadFile(final String directory, final String fileName) {
		try {
			return new String(Files.readAllBytes(getPath(directory, fileName)), UTF_8.toString());
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static Path getPath(final String directory, final String resourceName) throws IOException {
		final URI uri = AssetUtil.getURI(directory, resourceName);
		if (uri.toString().contains("!")) {
			return getPathFromFileSystem(uri);
		} else {
			return Paths.get(uri);
		}
	}

	private static Path getPathFromFileSystem(final URI uri) throws IOException {
		final String[] array = uri.toString().split("!");
		final URI fsuri = URI.create(array[0]);
		FileSystem fs = null;
		try {
			fs = FileSystems.newFileSystem(fsuri, Collections.emptyMap());
		} catch (final FileSystemAlreadyExistsException e2) {
			fs = FileSystems.getFileSystem(fsuri);
		}
		return fs.getPath(array[1]);
	}
}
