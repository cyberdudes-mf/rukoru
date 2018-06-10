package hoshisugi.rukoru.app.models.video;

import java.net.URL;
import java.util.function.Supplier;

import hoshisugi.rukoru.framework.util.AssetUtil;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class VideoFile extends VideoItem {

	private final Supplier<URL> urlSupplier;

	public VideoFile(final String name, final Supplier<URL> urlSupplier) {
		super(name);
		this.urlSupplier = urlSupplier;
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
		return urlSupplier.get();
	}

	@Override
	public Image getIcon() {
		return AssetUtil.getImage("16x16/video.png");
	}

}
