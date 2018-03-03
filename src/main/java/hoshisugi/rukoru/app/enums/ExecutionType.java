package hoshisugi.rukoru.app.enums;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ExecutionType {
	Service("1", "SERVICE"), EXE("2", "EXE"), bat("3", "BAT");

	private final String id;
	private final String value;

	private static final Map<String, String> index = Stream.of(values())
			.collect(Collectors.toMap(ExecutionType::getId, ExecutionType::toString));

	private ExecutionType(final String id, final String value) {
		this.id = id;
		this.value = value;
	}

	private String getId() {
		return id;
	}

	@Override
	public String toString() {
		return value;
	}

	public static String of(final String id) {
		return index.get(id);
	}
}
