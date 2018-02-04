package hoshisugi.rukoru.framework.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

}
