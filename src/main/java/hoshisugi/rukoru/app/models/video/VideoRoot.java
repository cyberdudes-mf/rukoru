package hoshisugi.rukoru.app.models.video;

import hoshisugi.rukoru.framework.util.AssetUtil;
import javafx.scene.image.Image;

public class VideoRoot extends VideoContainer {

	public VideoRoot() {
		super("Animation");
	}

	@Override
	public Image getIcon() {
		return AssetUtil.getImage("16x16/megumi.png");
	}

}
