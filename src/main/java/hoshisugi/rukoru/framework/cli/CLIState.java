package hoshisugi.rukoru.framework.cli;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class CLIState implements AutoCloseable {

	public static final int SUCCESS = 0;

	private final Process process;
	private final InputStream inputStream;
	private final InputStream errorStream;

	public CLIState(final Process process, final InputStream inputStream, final InputStream errorStream) {
		this.process = process;
		this.inputStream = inputStream;
		this.errorStream = errorStream;
	}

	public boolean isRunning() {
		return process.isAlive();
	}

	public boolean isSuccess() {
		return process.exitValue() == SUCCESS;
	}

	public boolean isFailure() {
		return process.exitValue() != SUCCESS;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public InputStream getErrorStream() {
		return errorStream;
	}

	public int waitFor() throws InterruptedException {
		return process.waitFor();
	}

	public boolean waitFor(final long timeout, final TimeUnit unit) throws InterruptedException {
		return process.waitFor(timeout, unit);
	}

	public void destroy() {
		process.destroy();
	}

	@Override
	public void close() throws Exception {
		process.destroy();
		inputStream.close();
		errorStream.close();
	}

}
