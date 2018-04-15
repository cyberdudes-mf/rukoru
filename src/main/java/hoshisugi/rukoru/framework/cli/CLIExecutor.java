package hoshisugi.rukoru.framework.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import com.google.common.collect.Lists;

import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import hoshisugi.rukoru.framework.util.IOUtil;

class CLIExecutor {

	static CLIState execute(final CLIParameter parameter) {
		final ProcessBuilder builder = new ProcessBuilder();
		setCommand(builder, parameter);
		setDirectory(builder, parameter);
		return runProcess(builder, parameter);
	}

	private static CLIState runProcess(final ProcessBuilder builder, final CLIParameter parameter) {
		ExecutorService executor = null;
		CLIState cliState = null;
		try {
			final Process process = builder.start();
			cliState = new CLIState(process, parameter.getCallback());
			final CountDownLatch latch = new CountDownLatch(1);
			final SuccessMonitor successMonitor = new SuccessMonitor(cliState, latch, parameter.getSuccessCondition());
			final FailMonitor failureMonitor = new FailMonitor(cliState, latch, parameter.getFailureCondition());
			final TimeoutMonitor timeoutMonitor = new TimeoutMonitor(parameter.getTimeout(), parameter.getTimeoutUnit(),
					latch, cliState::fail);
			executor = Executors.newFixedThreadPool(3);
			final List<Future<Void>> futures = Arrays.asList(executor.submit(successMonitor),
					executor.submit(failureMonitor), executor.submit(timeoutMonitor));
			ConcurrentUtil.run(() -> {
				try {
					latch.await();
				} finally {
					futures.forEach(f -> f.cancel(true));
				}
			});
			return cliState;
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

	static abstract class FinishedMonitor implements Callable<Void> {

		protected final CLIState state;
		protected final CountDownLatch latch;
		protected final PipedOutputStream output;
		protected final Predicate<String> condition;

		public FinishedMonitor(final CLIState state, final CountDownLatch latch, final Predicate<String> condition)
				throws IOException {
			this.state = state;
			this.latch = latch;
			this.condition = condition;
			this.output = new PipedOutputStream();
		}

		@Override
		public Void call() throws Exception {
			final BufferedReader reader = IOUtil.newBufferedReader(getInputStream(), Charset.forName("MS932"));
			try (PrintWriter writer = new PrintWriter(output)) {
				for (String line = null; (line = reader.readLine()) != null;) {
					writer.println(line);
					writer.flush();
					if (condition != null && condition.test(line)) {
						latch.countDown();
						updateState();
					}
				}
				postCall();
			}
			return null;
		}

		abstract InputStream getInputStream();

		abstract void updateState();

		void postCall() {
		}
	}

	static class SuccessMonitor extends CLIExecutor.FinishedMonitor {

		public SuccessMonitor(final CLIState state, final CountDownLatch latch, final Predicate<String> condition)
				throws IOException {
			super(state, latch, condition);
			state.setInputStream(new PipedInputStream(output));
		}

		@Override
		InputStream getInputStream() {
			return state.getProcess().getInputStream();
		}

		@Override
		void updateState() {
			state.succeed();
		}

		@Override
		void postCall() {
			if (latch.getCount() > 0) {
				latch.countDown();
				updateState();
			}
		}

	}

	static class FailMonitor extends CLIExecutor.FinishedMonitor {

		public FailMonitor(final CLIState state, final CountDownLatch latch, final Predicate<String> condition)
				throws IOException {
			super(state, latch, condition);
			state.setErrorStream(new PipedInputStream(output));
		}

		@Override
		InputStream getInputStream() {
			return state.getProcess().getErrorStream();
		}

		@Override
		void updateState() {
			state.fail();
		}

	}

	static class TimeoutMonitor implements Callable<Void> {

		private final long timeout;
		private final TimeUnit timeoutUnit;
		private final CountDownLatch latch;
		private final Runnable callback;

		public TimeoutMonitor(final long timeout, final TimeUnit timeoutUnit, final CountDownLatch latch,
				final Runnable callback) {
			this.timeout = timeout;
			this.timeoutUnit = timeoutUnit;
			this.latch = latch;
			this.callback = callback;
		}

		@Override
		public Void call() throws Exception {
			if (timeoutUnit != null) {
				timeoutUnit.sleep(timeout);
				latch.countDown();
				callback.run();
			}
			return null;
		}
	}
}
