package hoshisugi.rukoru.framework.util;

import java.util.LinkedHashMap;
import java.util.Map;

import javafx.event.EventHandler;
import javafx.stage.WindowEvent;

public class ShutdownHandler implements EventHandler<WindowEvent> {

	private static final Map<String, EventHandler<WindowEvent>> handlers = new LinkedHashMap<>();

	static {
		FXUtil.getPrimaryStage().setOnCloseRequest(new ShutdownHandler());
	}

	private ShutdownHandler() {
	}

	@Override
	public void handle(final WindowEvent event) {
		for (final EventHandler<WindowEvent> handler : handlers.values()) {
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
}
