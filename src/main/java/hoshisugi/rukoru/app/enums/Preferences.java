package hoshisugi.rukoru.app.enums;

public enum Preferences {

	ImageUrl("Home", "imageUrl"),
	RedmineLoginId("Redmine", "loginId"),
	RedminePassword("Redmine", "password")
	;
	

	private final String category;
	private final String key;

	private Preferences(final String category, final String key) {
		this.category = category;
		this.key = key;
	}

	public String getCategory() {
		return category;
	}

	public String getKey() {
		return key;
	}
}
