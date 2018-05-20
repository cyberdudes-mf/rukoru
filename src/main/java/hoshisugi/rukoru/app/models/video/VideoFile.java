package hoshisugi.rukoru.app.models.video;

import java.net.URL;

import hoshisugi.rukoru.framework.util.AssetUtil;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class VideoFile extends VideoItem {

	private final URL url;

	public VideoFile(final String name, final URL url) {
		super(name);
		this.url = url;
	}

	@Override
	public TreeItem<VideoItem> toTreeItem() {
		return new TreeItem<>(this, new ImageView(getIcon()));
	}

	@Override
	public boolean isFile() {
		return true;
	}

	public URL getUrl() {
		return url;
	}

	@Override
	public Image getIcon() {
		return AssetUtil.getImage("16x16/video.png");
	}

}
