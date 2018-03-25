package hoshisugi.rukoru.app.models.ds;

import java.util.Timer;
import java.util.TimerTask;

import hoshisugi.rukoru.framework.util.FXUtil;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.TextArea;
import javafx.stage.WindowEvent;

public class DSLogWriter {

	private final StringBuilder builder = new StringBuilder();
	private final TextArea textArea;
	private Timer timer;
	private final EventHandler<WindowEvent> onClose = e -> timer.cancel();

	public DSLogWriter(final TextArea textArea) {
		this.textArea = textArea;
	}

	public void writeLine(final String text) {
		if (timer == null) {
			final TimerTask task = new TimerTask() {
				@Override
				public void run() {
					if (builder.length() > 0) {
						textArea.appendText(builder.toString());
						builder.setLength(0);
					}
				}
			};

			timer = new Timer();
			timer.schedule(task, 0L, 50);
			FXUtil.getPrimaryStage().setOnCloseRequest(onClose);
		}
		builder.append(text).append(System.lineSeparator());
	}

	public void shutDown() {
		timer.cancel();
		textArea.appendText(builder.toString());
		FXUtil.getPrimaryStage().removeEventHandler(new EventType<WindowEvent>("WindowEvent"), onClose);
	}
}
