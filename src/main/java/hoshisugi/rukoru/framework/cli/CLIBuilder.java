package hoshisugi.rukoru.framework.cli;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CLIBuilder {

	private final CLIParameter parameter;

	public CLIBuilder(final String command) {
		this.parameter = new CLIParameter(command);
	}

	public CLIBuilder options(final String... options) {
		parameter.setOptions(Arrays.asList(options));
		return this;
	}

	public CLIBuilder directory(final Path directory) {
		parameter.setDirectory(directory.toFile());
		return this;
	}

	public CLIBuilder env(final Consumer<Map<String, String>> consumer) {
		parameter.setEnv(consumer);
		return this;
	}

	public CLIBuilder timeout(final long milliseconds) {
		return timeout(milliseconds, TimeUnit.MILLISECONDS);
	}

	public CLIBuilder timeout(final long time, final TimeUnit unit) {
		parameter.setTimeout(time);
		parameter.setTimeoutUnit(unit);
		return this;
	}

	public CLIBuilder successCondition(final Predicate<String> successCondition) {
		parameter.setSuccessCondition(successCondition);
		return this;
	}

	public CLIBuilder failureCondition(final Predicate<String> condition) {
		parameter.setFailureCondition(condition);
		return this;
	}

	public CLIBuilder callback(final Consumer<CLIState> callback) {
		parameter.setCallback(callback);
		return this;
	}

	public CLIState execute() {
		return CLIExecutor.execute(parameter);
	}
}
