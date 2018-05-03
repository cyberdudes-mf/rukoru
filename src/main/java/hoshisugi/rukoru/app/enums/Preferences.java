package hoshisugi.rukoru.app.enums;

public enum Preferences {
	ImageUrl("Home", "imageUrl", "https://s3-ap-northeast-1.amazonaws.com/com.appresso.dsc.rukoru/assets/top.jpg"),
	RedmineLoginId("Redmine", "loginId", null),
	RedminePassword("Redmine", "password", null),
	RedmineDefaultProject("Redmine", "defaultProject", null),
	RedmineDefaultVersion("Redmine", "defaultVersion", null),;

	private final String category;
	private final String key;
	private final String defaultValue;

	private Preferences(final String category, final String key, final String defaultValue) {
		this.category = category;
		this.key = key;
		this.defaultValue = defaultValue;
	}

	public String category() {
		return category;
	}

	public String key() {
		return key;
	}

	public String defaultValue() {
		return defaultValue;
	}
}