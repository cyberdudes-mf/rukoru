package hoshisugi.rukoru.framework.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

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
}
