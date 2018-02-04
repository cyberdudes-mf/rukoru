package hoshisugi.rukoru.app.enums;

public enum Preferences {
	HomeImageUrl("Home", "imageUrl", "https://s3-ap-northeast-1.amazonaws.com/com.appresso.dsc.rukoru/assets/top.jpg"),
	;

	private final String category;
	private final String key;
	private final String defaultValue;

	private Preferences(final String category, final String key, final String defaultValue) {
		this.category = category;
		this.key = key;
		this.defaultValue = defaultValue;
	}

	public String getCategory() {
		return category;
	}

	public String getKey() {
		return key;
	}

	public String getDefaultValue() {
		return defaultValue;
	}
}
