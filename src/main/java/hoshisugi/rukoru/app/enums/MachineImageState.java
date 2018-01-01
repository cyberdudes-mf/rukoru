package hoshisugi.rukoru.app.enums;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum MachineImageState {

	Pending("pending"), Available("available"), Invalid("invalid"), Deregistered("deregistered"), Transient(
			"transient"), Failed("failed"), Error("error");

	private static final Map<String, MachineImageState> index = Stream.of(values())
			.collect(Collectors.toMap(MachineImageState::toString, Function.identity()));

	private final String value;

	private MachineImageState(final String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return this.value;
	}

	public static MachineImageState of(final String value) {
		return index.get(value);
	}
}
