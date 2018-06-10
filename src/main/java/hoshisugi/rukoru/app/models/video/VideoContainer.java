package hoshisugi.rukoru.app.models.video;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;

public abstract class VideoContainer extends VideoItem {

	private final List<VideoItem> items = new ArrayList<>();

	public VideoContainer(final String name) {
		super(name);
	}

	@Override
	public TreeItem<VideoItem> toTreeItem() {
		final TreeItem<VideoItem> item = new TreeItem<>(this, new ImageView(getIcon()));
		final ObservableList<TreeItem<VideoItem>> children = item.getChildren();
		items.stream().map(VideoItem::toTreeItem).forEach(children::add);
		return item;
	}

	@Override
	public boolean isFile() {
		return false;
	}

	public void add(final VideoItem item) {
		items.add(item);
	}
}
