package hoshisugi.rukoru.app.enums;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum CSSThemes {
	MODENA("Modena"), CONSOLE("Console", "Common");

	private String displayName;

	private String[] values;

	private CSSThemes(final String... values) {
		this.displayName = values[0];
		this.values = values;
	}

	private static final Map<String, CSSThemes> index = Stream.of(values())
			.collect(Collectors.toMap(CSSThemes::toString, Function.identity()));

	public String[] getValues() {
		return values;
	}

	@Override
	public String toString() {
		return displayName;
	}

	public static CSSThemes of(final String displayName) {
		return index.get(displayName);
	}

}
