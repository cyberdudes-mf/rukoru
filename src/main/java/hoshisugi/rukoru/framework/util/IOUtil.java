package hoshisugi.rukoru.framework.util;

import static java.nio.file.StandardOpenOption.CREATE;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Supplier;

import hoshisugi.rukoru.app.models.common.AsyncResult;
import hoshisugi.rukoru.app.models.common.ContentInfo;

public class IOUtil {

	public static String readAll(final InputStream stream) throws IOException {
		return readAll(stream, Charset.defaultCharset());
	}

	public static String readAll(final InputStream stream, final Charset charset) throws IOException {
		try (InputStream in = stream; ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			final byte[] buff = new byte[8192];
			int size = 0;
			while ((size = in.read(buff)) > 0) {
				out.write(buff, 0, size);
			}
			final String charsetStr = charset != null ? charset.toString() : Charset.defaultCharset().toString();
			return out.toString(charsetStr);
		}
	}

	public static BufferedReader newBufferedReader(final InputStream input) {
		return newBufferedReader(input, Charset.defaultCharset());
	}

	public static BufferedReader newBufferedReader(final InputStream input, final Charset charset) {
		return new BufferedReader(new InputStreamReader(input, charset));
	}

	public static void redirectAsync(final InputStream input, final Path destination) {
		ConcurrentUtil.run(() -> redirect(input, destination));
	}

	public static void redirect(final InputStream input, final Path destination) throws IOException {
		try (final BufferedReader reader = newBufferedReader(input);
				final BufferedWriter writer = Files.newBufferedWriter(destination, StandardOpenOption.CREATE)) {
			for (String line = null; (line = reader.readLine()) != null;) {
				writer.write(line);
				writer.newLine();
			}
		}
	}

	public static AsyncResult downloadContent(final URL url, final Path destination) throws IOException {
		final Supplier<ContentInfo> contentSupplier = () -> {
			try {
				final URLConnection conn = url.openConnection();
				final int contentLength = Integer.parseInt(conn.getHeaderField("Content-Length"));
				final InputStream inputStream = conn.getInputStream();
				return new ContentInfo(contentLength, inputStream);
			} catch (final IOException e) {
				throw new UncheckedIOException(e);
			}
		};
		return downloadContent(contentSupplier, destination);
	}

	public static AsyncResult downloadContent(final Supplier<ContentInfo> contentSupplier, final Path destination) {
		final AsyncResult result = new AsyncResult();

		ConcurrentUtil.run(() -> {
			final ContentInfo content = contentSupplier.get();
			result.setName(destination.getFileName().toString());
			result.setTotal(content.getContentLength());
			final byte[] buff = new byte[1048576];
			try (InputStream input = content.getInputStream();
					OutputStream output = new BufferedOutputStream(Files.newOutputStream(destination, CREATE))) {
				int read;
				while ((read = input.read(buff)) >= 0) {
					output.write(buff, 0, read);
					result.addCurrent(read);
				}
			} catch (final Throwable e) {
				result.setThrown(e);
			}
		});
		return result;
	}
}
