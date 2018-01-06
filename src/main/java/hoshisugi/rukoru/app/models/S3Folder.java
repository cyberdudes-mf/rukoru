package hoshisugi.rukoru.app.models;

import static hoshisugi.rukoru.app.models.S3Item.Type.Folder;

import hoshisugi.rukoru.flamework.util.AssetUtil;
import javafx.scene.image.Image;

public class S3Folder extends S3Item {

	public S3Folder(final String bucketName, final String name) {
		setBucketName(bucketName);
		setKey(name);
		setName(name);
	}

	@Override
	public Type getType() {
		return Folder;
	}

	@Override
	public Image getIcon() {
		return AssetUtil.getImage("16x16/folder.png");
	}

}
