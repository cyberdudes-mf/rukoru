package hoshisugi.rukoru.app.models.hidden;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import hoshisugi.rukoru.framework.util.AssetUtil;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.input.KeyCode;

public class HiddenManager {

	private static final BooleanProperty hidden = new SimpleBooleanProperty(null, "hidden", true);

	private static final Properties properties = AssetUtil.loadProperties("setting.properties");

	private static final List<KeyCode> releaseCode;

	private static Queue<KeyCode> codes;

	static {
		releaseCode = Stream.of(properties.getProperty("release.code").split(",")).map(KeyCode::valueOf)
				.collect(Collectors.toList());
		initialize();
	}

	public static void initialize() {
		codes = new ArrayDeque<>(releaseCode);
	}

	public static boolean isHidden() {
		return hidden.get();
	}

	public static void setHidden(final boolean hidden) {
		HiddenManager.hidden.set(hidden);
	}

	public static BooleanProperty hiddenProperty() {
		return hidden;
	}

	public static boolean canShowHidden(final KeyCode code) {
		synchronized (codes) {
			if (code == codes.peek()) {
				codes.poll();
			} else {
				initialize();
			}
			if (codes.isEmpty()) {
				setHidden(false);
			}
		}
		return !isHidden();
	}
}
