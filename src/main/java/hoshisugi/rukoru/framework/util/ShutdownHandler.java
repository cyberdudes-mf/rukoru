package hoshisugi.rukoru.framework.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ShutdownHandler implements EventHandler<WindowEvent> {

	private static final Map<String, EventHandler<WindowEvent>> handlers = new LinkedHashMap<>();

	private static boolean shuttingDown;

	public static void init() {
		final Stage ps = FXUtil.getPrimaryStage();
		if (ps.getOnCloseRequest() != null) {
			throw new IllegalStateException("onCloseRequest に ShutdownHandler 以外のハンドラが登録されています。");
		}
		ps.setOnCloseRequest(new ShutdownHandler());
	}

	private ShutdownHandler() {
	}

	@Override
	public void handle(final WindowEvent event) {
		shuttingDown = true;
		// ConcurrentModificationException 回避のため、List に詰め替える
		final List<EventHandler<WindowEvent>> h = handlers.values().stream().collect(Collectors.toList());
		for (final EventHandler<WindowEvent> handler : h) {
			try {
				handler.handle(event);
			} catch (final Exception e) {
				// すべてのハンドラを実行するため例外を無視する
			}
		}
	}

	public static void addHandler(final String key, final EventHandler<WindowEvent> handler) {
		handlers.put(key, handler);
	}

	public static void removeHandler(final String key) {
		handlers.remove(key);
	}

	public static boolean isShuttingDown() {
		return shuttingDown;
	}
}
