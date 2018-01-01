package hoshisugi.rukoru.app.enums;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum EC2InstanceState {
	Pending("pending"), Running("running"), ShuttingDown("shutting-down"), Terminated("terminated"), Stopping(
			"stopping"), Stopped("stopped");

	private static final Map<String, EC2InstanceState> index = Stream.of(values())
			.collect(Collectors.toMap(EC2InstanceState::toString, Function.identity()));

	private String value;

	private EC2InstanceState(final String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return this.value;
	}

	public static EC2InstanceState of(final String value) {
		return index.get(value);
	}
}
