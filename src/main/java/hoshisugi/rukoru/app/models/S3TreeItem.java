package hoshisugi.rukoru.app.models;

import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;

public class S3TreeItem extends TreeItem<S3Item> {

	private boolean leaf;

	public S3TreeItem(final S3Item item) {
		super(item, new ImageView(item.getIcon()));
	}

	@Override
	public boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(final boolean leaf) {
		this.leaf = leaf;
	}

}
