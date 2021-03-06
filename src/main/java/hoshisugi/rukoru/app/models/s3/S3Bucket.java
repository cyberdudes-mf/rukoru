package hoshisugi.rukoru.app.models.s3;

import static hoshisugi.rukoru.app.models.s3.S3Item.Type.Bucket;

import com.amazonaws.services.s3.model.Bucket;

import hoshisugi.rukoru.framework.util.AssetUtil;
import hoshisugi.rukoru.framework.util.DateTimeUtil;
import javafx.scene.image.Image;

public class S3Bucket extends S3Item {

	public S3Bucket(final Bucket bucket) {
		setBucketName(bucket.getName());
		setName(bucket.getName());
		setLastModified(DateTimeUtil.toString(bucket.getCreationDate()));
		if (bucket.getOwner() != null) {
			setOwner(bucket.getOwner().getDisplayName());
		}
	}

	@Override
	public S3Item.Type getType() {
		return Bucket;
	}

	@Override
	public Image getIcon() {
		return AssetUtil.getImage("16x16/s3bucket.png");
	}

}
