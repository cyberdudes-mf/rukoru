package hoshisugi.rukoru.app.models.ds;

import java.util.Timer;
import java.util.TimerTask;

import hoshisugi.rukoru.framework.util.ShutdownHandler;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class DSLogWriter {

	private static final int BUFFERE_SIZE = 80000;
	private final StringBuilder builder = new StringBuilder();
	private final TextArea textArea;
	private Timer timer;

	public DSLogWriter(final TextArea textArea) {
		this.textArea = textArea;
		textArea.clear();
	}

	public void writeLine(final String text) {
		if (timer == null) {
			final TimerTask task = new TimerTask() {
				@Override
				public void run() {
					if (builder.length() > 0) {
						Platform.runLater(() -> {
							final int tLength = textArea.getLength();
							final int length = tLength + builder.length();
							if (length > BUFFERE_SIZE) {
								final int end = length - BUFFERE_SIZE;
								if (tLength > end) {
									textArea.deleteText(0, end);
								}
							}
							textArea.appendText(builder.toString());
							builder.setLength(0);
						});
					}
				}
			};

			timer = new Timer();
			timer.schedule(task, 0L, 50);
			ShutdownHandler.addHandler(toString(), e -> this.shutDown());
		}
		builder.append(text).append(System.lineSeparator());
	}

	public void shutDown() {
		ShutdownHandler.removeHandler(toString());
		if (timer != null) {
			timer.cancel();
		}
		if (builder.length() > 0) {
			Platform.runLater(() -> textArea.appendText(builder.toString()));
		}
	}
}
