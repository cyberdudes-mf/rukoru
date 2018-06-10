package hoshisugi.rukoru.framework.converter;

import java.net.MalformedURLException;
import java.net.URL;

import javafx.util.StringConverter;

public class URLStringConverter extends StringConverter<URL> {

	@Override
	public String toString(final URL url) {
		if (url == null) {
			return null;
		}
		return url.toString();
	}

	@Override
	public URL fromString(final String string) {
		try {
			return new URL(string);
		} catch (final MalformedURLException e) {
			return null;
		}
	}

}
