package hoshisugi.rukoru.framework.database.builder;

public class Column {

	private final String name;
	private final Object value;

	public static Column $(final String name, final Object value) {
		return new Column(name, value);
	}

	private Column(final String name, final Object value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}

}
