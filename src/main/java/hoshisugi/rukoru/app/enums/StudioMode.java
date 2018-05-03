package hoshisugi.rukoru.app.enums;

import java.util.stream.Stream;

public enum StudioMode {
	Desktop("1", "for Desktop"),
	Silverlight("2", "for Web(Silverlight)"),
	WPF("3", "for Web(WPF)");

	private final String id;
	private final String displayName;

	private StudioMode(final String id, final String displayName) {
		this.id = id;
		this.displayName = displayName;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return displayName;
	}

	public static StudioMode of(final String id) {
		return Stream.of(values()).filter(t -> t.getId().equals(id)).findFirst().get();
	}
}
