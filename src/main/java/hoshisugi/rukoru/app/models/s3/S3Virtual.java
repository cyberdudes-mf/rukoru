package hoshisugi.rukoru.app.models.s3;

import hoshisugi.rukoru.framework.util.AssetUtil;
import javafx.scene.image.Image;

public class S3Virtual extends S3Folder {

	public S3Virtual(final String bucketName, final String name) {
		super(bucketName, name);
	}

	@Override
	public Image getIcon() {
		return AssetUtil.getImage("16x16/virtual.png");
	}

}
