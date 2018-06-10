package hoshisugi.rukoru.framework.converter;

import javafx.scene.paint.Color;
import javafx.util.StringConverter;

public class ColorStringConverter extends StringConverter<Color> {

	@Override
	public String toString(final Color color) {
		if (color == null) {
			return null;
		}
		return String.format("#%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255),
				(int) (color.getBlue() * 255));
	}

	@Override
	public Color fromString(final String string) {
		try {
			return Color.web(string);
		} catch (final Exception e) {
			return null;
		}
	}

}
