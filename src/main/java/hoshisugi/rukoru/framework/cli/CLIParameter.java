package hoshisugi.rukoru.framework.cli;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

class CLIParameter {

	private final String command;
	private Collection<String> options;
	private File directory;
	private long timeout;
	private TimeUnit timeoutUnit;
	private Predicate<String> successCondition;
	private Predicate<String> failureCondition;
	private Consumer<CLIState> callback;

	CLIParameter(final String command) {
		this.command = command;
	}

	void setOptions(final Collection<String> options) {
		this.options = options;
	}

	void setSuccessCondition(final Predicate<String> predicate) {
		this.successCondition = predicate;
	}

	void setFailureCondition(final Predicate<String> predicate) {
		this.failureCondition = predicate;
	}

	String getCommand() {
		return command;
	}

	Collection<String> getOptions() {
		return options;
	}

	File getDirectory() {
		return directory;
	}

	void setDirectory(final File directory) {
		this.directory = directory;
	}

	void timeout(final long timeout, final TimeUnit timeoutUnit) {
		this.timeout = timeout;
		this.timeoutUnit = timeoutUnit;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(final long timeout) {
		this.timeout = timeout;
	}

	public TimeUnit getTimeoutUnit() {
		return timeoutUnit;
	}

	public void setTimeoutUnit(final TimeUnit timeoutUnit) {
		this.timeoutUnit = timeoutUnit;
	}

	public Predicate<String> getSuccessCondition() {
		return successCondition;
	}

	public Predicate<String> getFailureCondition() {
		return failureCondition;
	}

	public Consumer<CLIState> getCallback() {
		return callback;
	}

	public void setCallback(final Consumer<CLIState> callback) {
		this.callback = callback;
	}

}
