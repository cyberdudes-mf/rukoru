package hoshisugi.rukoru.framework.cli;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class CLIState implements AutoCloseable {

	public static final int SUCCESS = 0;

	private final Process process;
	private InputStream inputStream;
	private InputStream errorStream;
	private final Consumer<CLIState> callback;
	private boolean success;
	private boolean failure;

	CLIState(final Process process, final Consumer<CLIState> callback) {
		this.process = process;
		this.callback = callback;
	}

	public boolean isRunning() {
		return process.isAlive();
	}

	public boolean isSuccess() {
		return success;
	}

	public boolean isFailure() {
		return failure;
	}

	void succeed() {
		this.success = true;
		if (callback != null) {
			callback.accept(this);
		}
	}

	void fail() {
		this.failure = true;
		if (callback != null) {
			callback.accept(this);
		}
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public InputStream getErrorStream() {
		return errorStream;
	}

	void setInputStream(final InputStream inputStream) {
		this.inputStream = inputStream;
	}

	void setErrorStream(final InputStream errorStream) {
		this.errorStream = errorStream;
	}

	Process getProcess() {
		return process;
	}

	public int waitFor() throws InterruptedException {
		return process.waitFor();
	}

	public boolean waitFor(final long timeout, final TimeUnit unit) throws InterruptedException {
		return process.waitFor(timeout, unit);
	}

	public void destroy() {
		process.destroyForcibly();
	}

	public int getExitValue() {
		return process.exitValue();
	}

	@Override
	public void close() throws Exception {
		process.destroy();
		inputStream.close();
		errorStream.close();
	}

}
