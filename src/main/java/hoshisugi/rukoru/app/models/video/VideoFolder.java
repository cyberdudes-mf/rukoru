package hoshisugi.rukoru.app.models.video;

import hoshisugi.rukoru.framework.util.AssetUtil;
import javafx.scene.image.Image;

public class VideoFolder extends VideoContainer {

	public VideoFolder(final String name) {
		super(name);
	}

	@Override
	public Image getIcon() {
		return AssetUtil.getImage("16x16/folder.png");
	}

}
