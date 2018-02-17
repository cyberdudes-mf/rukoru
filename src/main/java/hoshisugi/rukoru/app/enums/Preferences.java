package hoshisugi.rukoru.app.enums;

public interface Preferences {

	public static enum Home {
		ImageUrl("Home", "imageUrl");

		private final String category;
		private final String key;

		private Home(final String category, final String key) {
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
}
