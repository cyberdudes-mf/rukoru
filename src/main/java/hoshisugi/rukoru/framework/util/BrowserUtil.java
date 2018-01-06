package hoshisugi.rukoru.framework.util;

import java.awt.Desktop;
import java.net.URI;

public class BrowserUtil {

	public static void browse(final String url) throws Exception {
		Desktop.getDesktop().browse(new URI(url));
	}
}
