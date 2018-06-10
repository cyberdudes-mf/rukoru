package hoshisugi.rukoru.app.models.video;

import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;

public abstract class VideoItem {

	private final String name;

	public VideoItem(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public abstract TreeItem<VideoItem> toTreeItem();

	public abstract Image getIcon();

	public abstract boolean isFile();

	@Override
	public String toString() {
		return getName();
	}

}
