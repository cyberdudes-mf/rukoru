package hoshisugi.rukoru.app.enums;

public enum DSProperties {
	Properties("Properties", null), DSlogProperties("dslog.properties",
			"\\server\\conf\\dslog.properties"), LocaleProperties("locale.properties",
					"\\server\\conf\\locale.properties"), SystemProperties("system.properties",
							"\\server\\conf\\system.properties");

	private final String displayName;

	private final String path;

	private DSProperties(final String displayName, final String path) {
		this.displayName = displayName;
		this.path = path;
	}

	@Override
	public String toString() {
		return displayName;
	}

	public String getPath() {
		return path;
	}

}
