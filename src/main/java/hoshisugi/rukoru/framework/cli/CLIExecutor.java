package hoshisugi.rukoru.framework.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import com.google.common.collect.Lists;

class CLIExecutor {

	static CLIState execute(final CLIParameter parameter) {
		final ProcessBuilder builder = new ProcessBuilder();
		setCommand(builder, parameter);
		setDirectory(builder, parameter);
		return runProcess(builder, parameter);
	}

	private static CLIState runProcess(final ProcessBuilder builder, final CLIParameter parameter) {
		ExecutorService executor = null;
		try {
			final CountDownLatch latch = new CountDownLatch(1);
			final Process process = builder.start();
			executor = Executors.newFixedThreadPool(3);
			final FinishedMonitor successMonitor = new FinishedMonitor(process.getInputStream(),
					parameter.getSuccessPredicate(), latch);
			final FinishedMonitor failureMonitor = new FinishedMonitor(process.getErrorStream(),
					parameter.getFailurePredicate(), latch);
			final List<Future<Void>> futures = Arrays.asList(executor.submit(successMonitor),
					executor.submit(failureMonitor),
					executor.submit(new TimeoutMonitor(parameter.getTimeout(), parameter.getTimeoutUnit(), latch)));
			latch.await();
			futures.forEach(f -> f.cancel(true));
			return new CLIState(process, successMonitor.getInput(), successMonitor.getInput());
		} catch (final Exception e) {
			throw new CLIException(String.format("コマンド[%s]の実行に失敗しました。", parameter.getCommand()), e);
		} finally {
			if (executor != null) {
				executor.shutdown();
			}
		}
	}

	protected static void setCommand(final ProcessBuilder builder, final CLIParameter parameter) {
		final List<String> commands = Lists.newArrayList("cmd", "/c", parameter.getCommand());
		final Collection<String> options = parameter.getOptions();
		if (options != null) {
			commands.addAll(options);
		}
		builder.command(commands);
	}

	protected static void setDirectory(final ProcessBuilder builder, final CLIParameter parameter) {
		final File directory = parameter.getDirectory();
		if (directory != null) {
			builder.directory(directory);
		}
	}

	static class FinishedMonitor implements Callable<Void> {

		private final InputStream stream;
		private final Predicate<String> predicate;
		private final CountDownLatch latch;
		private final PipedOutputStream output;
		private final PipedInputStream input;

		public FinishedMonitor(final InputStream stream, final Predicate<String> predicate, final CountDownLatch latch)
				throws IOException {
			this.stream = stream;
			this.predicate = predicate;
			this.latch = latch;
			this.output = new PipedOutputStream();
			this.input = new PipedInputStream(output);
		}

		public InputStream getInput() {
			return input;
		}

		@Override
		public Void call() throws Exception {
			final BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "MS932"));
			final Optional<Predicate<String>> optional = Optional.ofNullable(predicate);
			try (PrintWriter writer = new PrintWriter(output)) {
				String line;
				while ((line = reader.readLine()) != null) {
					writer.println(line);
					writer.flush();
					if (optional.isPresent() && optional.get().test(line)) {
						latch.countDown();
					}
				}
				latch.countDown();
			}
			return null;
		}

	}

	static class TimeoutMonitor implements Callable<Void> {

		private final long timeout;
		private final TimeUnit timeoutUnit;
		private final CountDownLatch latch;

		public TimeoutMonitor(final long timeout, final TimeUnit timeoutUnit, final CountDownLatch latch) {
			this.timeout = timeout;
			this.timeoutUnit = timeoutUnit;
			this.latch = latch;
		}

		@Override
		public Void call() throws Exception {
			if (timeoutUnit != null) {
				timeoutUnit.sleep(timeout);
				latch.countDown();
			}
			return null;
		}
	}
}
