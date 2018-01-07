package hoshisugi.rukoru.framework.util;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class BrowserUtil {

	public static void browse(final String url) {
		try {
			Desktop.getDesktop().browse(new URI(url));
		} catch (IOException | URISyntaxException e) {
			DialogUtil.showErrorDialog(e);
		}
	}
}
