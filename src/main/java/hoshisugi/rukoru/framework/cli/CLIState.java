package hoshisugi.rukoru.framework.cli;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class CLIState implements AutoCloseable {

	public static final int SUCCESS = 0;

	private final Process process;
	private InputStream inputStream;
	private InputStream errorStream;
	private boolean success;
	private boolean failure;

	public CLIState(final Process process) {
		this.process = process;
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
	}

	void fail() {
		this.failure = true;
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
