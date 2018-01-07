package hoshisugi.rukoru.app.services.s3;

import java.io.IOException;
import java.nio.file.Path;

import hoshisugi.rukoru.app.models.s3.DownloadObjectResult;
import hoshisugi.rukoru.app.models.s3.S3Bucket;
import hoshisugi.rukoru.app.models.s3.S3Folder;
import hoshisugi.rukoru.app.models.s3.S3Item;
import hoshisugi.rukoru.app.models.s3.S3Object;

public interface S3Service {

	void updateItems(S3Item item);

	DownloadObjectResult downloadObject(S3Item item, Path destination) throws IOException;

	S3Bucket createBucket(String bucketName);

	void deleteBucket(S3Bucket bucket);

	void deleteObject(S3Item item);

	S3Folder createFolder(String bucketName, String key);

	S3Object uploadObject(String bucketName, String key, Path path);
}
