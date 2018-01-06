package hoshisugi.rukoru.framework.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Platform;

public class ConcurrentUtil {

	public static void run(final AsyncTask task) {
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		try {
			executor.submit(() -> {
				try {
					task.run();
				} catch (final Throwable t) {
					Platform.runLater(() -> {
						DialogUtil.showErrorDialog(t);
					});
				}
			});
		} finally {
			executor.shutdown();
		}
	}

	@FunctionalInterface
	public static interface AsyncTask {
		void run() throws Exception;
	}
}
