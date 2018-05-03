package hoshisugi.rukoru.app.enums;

public enum ExecutionType {
	SERVICE("1"), EXE("2"), BAT("3");

	private final String id;

	private ExecutionType(final String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
