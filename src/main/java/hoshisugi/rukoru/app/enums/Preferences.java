package hoshisugi.rukoru.app.enums;

public enum Preferences {

	ImageUrl("Home", "imageUrl"),
	RedmineLoginId("Redmine", "loginId"),
	RedminePassword("Redmine", "password"),
	RedmineDefaultProject("Redmine", "defaultProject"),
	RedmineDefaultVersion("Redmine", "defaultVersion"),
	;
	

	private final String category;
	private final String key;

	private Preferences(final String category, final String key) {
		this.category = category;
		this.key = key;
	}

	public String category() {
		return category;
	}

	public String key() {
		return key;
	}
}
