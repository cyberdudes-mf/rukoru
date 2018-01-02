package hoshisugi.rukoru.app.services.ec2;

import java.util.List;

import hoshisugi.rukoru.app.models.S3Bucket;

public interface S3Service {

	List<S3Bucket> listBuckets();
}
