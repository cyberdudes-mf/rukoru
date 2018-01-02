package hoshisugi.rukoru.app.models;

import com.amazonaws.services.s3.model.Bucket;

import hoshisugi.rukoru.flamework.util.AssetUtil;
import javafx.scene.image.Image;

public class S3Bucket extends S3Item {

	public S3Bucket(final Bucket bucket) {
		setName(bucket.getName());
		setLastModified(bucket.getCreationDate());
		setOwner(bucket.getOwner().getDisplayName());
	}

	@Override
	public Image getIcon() {
		return AssetUtil.getImage("s3bucket_16x16.png");
	}

}
