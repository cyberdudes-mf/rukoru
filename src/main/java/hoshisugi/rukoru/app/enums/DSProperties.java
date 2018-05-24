package hoshisugi.rukoru.app.enums;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum DSProperties {
	DSlogProperties("dslog.properties", "/server/conf/dslog.properties"), LocaleProperties("locale.properties",
			"/server/conf/locale.properties"), SystemProperties("system.properties", "/server/conf/system.properties");

	private final String displayName;

	private final String path;

	private static final Map<String, DSProperties> index = Stream.of(values())
			.collect(Collectors.toMap(DSProperties::getDisplayName, Function.identity()));

	DSProperties(final String displayName, final String path) {
		this.displayName = displayName;
		this.path = path;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getPath() {
		return path;
	}

	public static DSProperties of(final String value) {
		return index.get(value);
	}

}
