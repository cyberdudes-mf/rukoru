package hoshisugi.rukoru.app.enums;

public enum ExecutionType {
	Service("Service", 1), EXE("EXE", 2), bat("bat", 3);

	private String displayName;
	private int id;

	private ExecutionType(final String displayName, final int id) {
		this.displayName = displayName;
		this.id = id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public int getId() {
		return id;
	}
}
