package hoshisugi.rukoru.app.enums;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import hoshisugi.rukoru.framework.util.AssetUtil;

public enum CSSThemes {
	MODENA("modena"), CONSOLE("console");

	private String value;

	private CSSThemes(final String value) {
		this.value = value;
	}

	private static final Map<String, CSSThemes> index = Stream.of(values())
			.collect(Collectors.toMap(CSSThemes::toString, Function.identity()));

	@Override
	public String toString() {
		return value;
	}

	public String getCSS() {
		return AssetUtil.getURL("css", value + ".css").toExternalForm();
	}

	public static CSSThemes of(final String value) {
		return index.get(value);
	}
}
