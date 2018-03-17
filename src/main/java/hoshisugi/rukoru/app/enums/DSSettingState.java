package hoshisugi.rukoru.app.enums;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum DSSettingState {
	Insert("Insert"), Update("Update"), Delete("Delete");

	private static final Map<String, DSSettingState> index = Stream.of(values())
			.collect(Collectors.toMap(DSSettingState::toString, Function.identity()));

	private String value;

	private DSSettingState(final String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return this.value;
	}

	public static DSSettingState of(final String value) {
		return index.get(value);
	}
}
