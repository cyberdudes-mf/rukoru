package hoshisugi.rukoru.app.enums;

import java.util.stream.Stream;

public enum ExecutionType {
	SERVICE("1", "SERVICE"), EXE("2", "EXE"), BAT("3", "BAT");

	private final String id;
	private final String value;

	private ExecutionType(final String id, final String value) {
		this.id = id;
		this.value = value;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return value;
	}

	public static ExecutionType of(final String id) {
		return Stream.of(values()).filter(t -> t.getId().equals(id)).findFirst().get();
	}
}
