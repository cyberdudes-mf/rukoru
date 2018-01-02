package hoshisugi.rukoru.app.models;

import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3Object extends S3Item {

	public S3Object(final S3ObjectSummary object) {
		setName(object.getKey());
		setLastModified(object.getLastModified());
		setSize(object.getSize());
		setStorageClass(object.getStorageClass());
		setOwner(object.getOwner().getDisplayName());
	}

}
