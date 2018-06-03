package hoshisugi.rukoru.app.services.s3;

import java.io.IOException;
import java.nio.file.Path;

import hoshisugi.rukoru.app.models.common.AsyncResult;
import hoshisugi.rukoru.app.models.s3.S3Bucket;
import hoshisugi.rukoru.app.models.s3.S3Folder;
import hoshisugi.rukoru.app.models.s3.S3Item;
import hoshisugi.rukoru.app.models.s3.UploadObjectResult;

public interface S3Service {

	void updateItems(S3Item item);

	AsyncResult downloadObject(S3Item item, Path destination) throws IOException;

	S3Bucket createBucket(String bucketName);

	void deleteBucket(S3Bucket bucket);

	void deleteObject(S3Item item);

	S3Folder createFolder(String bucketName, String key);

	UploadObjectResult uploadObject(String bucketName, String key, Path path) throws IOException;

	S3Item copyObject(String sourceBucketName, String sourceKey, String destinationBucketName, String destinationKey);

	S3Item moveObject(String sourceBucketName, String sourceKey, String destinationBucketName, String destinationKey);

	void publishObject(S3Item item);
}
