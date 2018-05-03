package hoshisugi.rukoru.app.enums;

import java.util.stream.Stream;

public enum ExecutionType {
	SERVICE("1"), EXE("2"), BAT("3");

	private final String id;

	private ExecutionType(final String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public static ExecutionType of(final String id) {
		return Stream.of(values()).filter(e -> e.getId().equals(id)).findFirst().orElse(null);
	}
}
