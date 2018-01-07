package hoshisugi.rukoru.app.services.s3;

import static com.amazonaws.regions.Regions.AP_NORTHEAST_1;
import static hoshisugi.rukoru.app.models.s3.S3Item.DELIMITER;
import static java.nio.file.StandardOpenOption.CREATE;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.common.base.Strings;

import hoshisugi.rukoru.app.models.auth.AuthSetting;
import hoshisugi.rukoru.app.models.s3.DownloadObjectResult;
import hoshisugi.rukoru.app.models.s3.S3Bucket;
import hoshisugi.rukoru.app.models.s3.S3Folder;
import hoshisugi.rukoru.app.models.s3.S3Item;
import hoshisugi.rukoru.app.models.s3.S3Object;
import hoshisugi.rukoru.app.models.s3.S3Root;
import hoshisugi.rukoru.framework.base.BaseService;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;

public class S3ServiceImpl extends BaseService implements S3Service {

	@Override
	public void updateItems(final S3Item item) {
		if (Strings.isNullOrEmpty(item.getBucketName())) {
			updateBuckets((S3Root) item);
		} else {
			updateObjects(item);
		}
		item.sort(Comparator.comparing(S3Item::getType).thenComparing(S3Item::getName));
	}

	private AmazonS3 createClient() {
		final AuthSetting authSetting = AuthSetting.get();
		final AWSCredentials credential = new BasicAWSCredentials(authSetting.getAccessKeyId(),
				authSetting.getSecretAccessKey());
		final AWSCredentialsProvider provider = new AWSStaticCredentialsProvider(credential);
		return AmazonS3ClientBuilder.standard().withCredentials(provider).withRegion(AP_NORTHEAST_1).build();
	}

	private void updateBuckets(final S3Root item) {
		final AmazonS3 client = createClient();
		final List<S3Bucket> buckets = client.listBuckets().stream().map(S3Bucket::new).collect(Collectors.toList());
		item.getItems().setAll(buckets);
		buckets.stream().forEach(i -> ConcurrentUtil.run(() -> updateItems(i)));
	}

	private void updateObjects(final S3Item item) {
		final List<S3Item> objects = new ArrayList<>();
		final List<String> folders = new ArrayList<>();
		listObject(item, result -> {
			result.getObjectSummaries().stream().peek(s -> {
				if (!isObject(s)) {
					folders.add(s.getKey());
				}
			}).filter(this::isObject).map(S3Object::new).forEach(objects::add);
		});
		toTreeStructure(item, objects, folders);
	}

	private ListObjectsRequest createListObjectsRequest(final S3Item item) {
		final ListObjectsRequest request = new ListObjectsRequest();
		request.setBucketName(item.getBucketName());
		final String key = item.getKey();
		if (!Strings.isNullOrEmpty(key)) {
			request.setPrefix(item.getKey());
		}
		return request;
	}

	private boolean isObject(final S3ObjectSummary object) {
		return !object.getKey().endsWith(DELIMITER);
	}

	private void toTreeStructure(final S3Item rootItem, final List<S3Item> objects, final List<String> folders) {
		final Map<String, S3Item> structured = new HashMap<>();
		structured.put(rootItem.getKey(), rootItem);
		rootItem.getItems().clear();
		objects.forEach(i -> storeInParent(structured, i));
		folders.stream().filter(f -> !structured.containsKey(f)).map(f -> new S3Folder(rootItem.getBucketName(), f))
				.peek(f -> structured.put(f.getKey(), f)).forEach(f -> storeInParent(structured, f));
	}

	private String getParentKey(final S3Item item) {
		final String key = item.getKey();
		if (!key.contains(DELIMITER)) {
			return "";
		} else if (key.endsWith(DELIMITER)) {
			return key.substring(0, key.lastIndexOf(DELIMITER, key.length() - 2) + 1);
		} else {
			return key.substring(0, key.lastIndexOf(DELIMITER) + 1);
		}
	}

	private void storeInParent(final Map<String, S3Item> structured, final S3Item item) {
		final String parentKey = getParentKey(item);
		if (structured.containsKey(parentKey)) {
			structured.get(parentKey).getItems().add(item);
		} else {
			final S3Folder parent = new S3Folder(item.getBucketName(), parentKey);
			parent.getItems().add(item);
			storeInParent(structured, parent);
			structured.put(parentKey, parent);
		}
	}

	@Override
	public DownloadObjectResult downloadObject(final S3Item item, final Path destination) throws IOException {
		final AmazonS3 client = createClient();
		final GetObjectRequest request = new GetObjectRequest(item.getBucketName(), item.getKey());
		final com.amazonaws.services.s3.model.S3Object object = client.getObject(request);

		final DownloadObjectResult result = new DownloadObjectResult();
		result.setContentLength(object.getObjectMetadata().getContentLength());
		ConcurrentUtil.run(() -> {
			final byte[] buff = new byte[8192];
			try (S3ObjectInputStream input = object.getObjectContent();
					OutputStream output = Files.newOutputStream(destination, CREATE)) {
				result.setStatus(DownloadObjectResult.Status.Downloading);
				int read;
				while ((read = input.read(buff)) >= 0) {
					output.write(buff, 0, read);
					result.addWrote(read);
				}
			} catch (final Exception e) {
				result.setThrown(e);
			} finally {
				result.setStatus(DownloadObjectResult.Status.Done);
			}
		});
		return result;
	}

	@Override
	public S3Bucket createBucket(final String bucketName) {
		final AmazonS3 client = createClient();
		final Bucket bucket = client.createBucket(bucketName);
		return client.listBuckets().stream().filter(b -> b.getName().equals(bucketName)).map(S3Bucket::new).findFirst()
				.orElseGet(() -> new S3Bucket(bucket));
	}

	@Override
	public void deleteBucket(final S3Bucket bucket) {
		final AmazonS3 client = createClient();
		client.deleteBucket(bucket.getBucketName());
	}

	@Override
	public void deleteObject(final S3Item item) {
		final AmazonS3 client = createClient();
		if (!item.isContainer()) {
			client.deleteObject(item.getBucketName(), item.getKey());
		} else {
			listObject(client, item, result -> {
				result.getObjectSummaries().stream()
						.forEach(s -> client.deleteObject(item.getBucketName(), s.getKey()));
			});
		}
	}

	private void listObject(final S3Item item, final Consumer<ObjectListing> consumer) {
		listObject(createClient(), item, consumer);
	}

	private void listObject(final AmazonS3 client, final S3Item item, final Consumer<ObjectListing> consumer) {
		final ListObjectsRequest request = createListObjectsRequest(item);
		ObjectListing result = null;
		do {
			result = client.listObjects(request);
			consumer.accept(result);
			request.setMarker(result.getNextMarker());
		} while (result.isTruncated());
	}

	@Override
	public S3Folder createFolder(final String bucketName, final String key) {
		if (!key.endsWith(DELIMITER)) {
			throw new IllegalArgumentException("The key must be end with \"/\".");
		}
		final AmazonS3 client = createClient();
		final ByteArrayInputStream emptyInputStream = new ByteArrayInputStream(new byte[0]);
		final ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0);
		client.putObject(bucketName, key, emptyInputStream, metadata);
		final ObjectListing listObjects = client.listObjects(bucketName, key);
		return listObjects.getObjectSummaries().stream().filter(s -> !isObject(s))
				.map(s -> new S3Folder(s.getBucketName(), s.getKey())).findFirst().get();
	}

	@Override
	public S3Object uploadObject(final String bucketName, final String key, final Path path) {
		if (!Files.exists(path)) {
			throw new IllegalArgumentException("File does not exist.");
		}
		final AmazonS3 client = createClient();
		client.putObject(bucketName, key, path.toFile());
		final ObjectListing listObjects = client.listObjects(bucketName, key);
		return listObjects.getObjectSummaries().stream().filter(this::isObject).map(S3Object::new).findFirst().get();
	}
}
