package hoshisugi.rukoru.flamework.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrentUtil {

	public static void run(final Runnable task) {
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		try {
			executor.submit(task);
		} finally {
			executor.shutdown();
		}
	}
}
