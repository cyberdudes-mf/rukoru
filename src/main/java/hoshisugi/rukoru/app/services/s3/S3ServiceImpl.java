package hoshisugi.rukoru.app.services.s3;

import static com.amazonaws.regions.Regions.AP_NORTHEAST_1;
import static com.amazonaws.services.s3.model.CannedAccessControlList.PublicRead;
import static hoshisugi.rukoru.app.models.s3.S3Item.DELIMITER;
import static hoshisugi.rukoru.framework.event.ShutdownHandler.isShuttingDown;
import static java.nio.file.StandardOpenOption.READ;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Region;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.common.base.Strings;

import hoshisugi.rukoru.app.models.common.AsyncResult;
import hoshisugi.rukoru.app.models.common.ContentInfo;
import hoshisugi.rukoru.app.models.s3.S3Bucket;
import hoshisugi.rukoru.app.models.s3.S3Folder;
import hoshisugi.rukoru.app.models.s3.S3Item;
import hoshisugi.rukoru.app.models.s3.S3Object;
import hoshisugi.rukoru.app.models.s3.S3Root;
import hoshisugi.rukoru.app.models.s3.S3Virtual;
import hoshisugi.rukoru.app.models.s3.UploadObjectResult;
import hoshisugi.rukoru.app.models.settings.Credential;
import hoshisugi.rukoru.framework.base.BaseService;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import hoshisugi.rukoru.framework.util.IOUtil;

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

	@Override
	public S3Bucket createBucket(final String bucketName) {
		final AmazonS3 client = createDefaultClient();
		final Bucket bucket = client.createBucket(bucketName);
		return client.listBuckets().stream().filter(b -> b.getName().equals(bucketName)).map(S3Bucket::new).findFirst()
				.orElseGet(() -> new S3Bucket(bucket));
	}

	@Override
	public void deleteBucket(final S3Bucket bucket) {
		final AmazonS3 client = createClient(bucket.getBucketName());
		client.deleteBucket(bucket.getBucketName());
	}

	@Override
	public void deleteObject(final S3Item item) {
		final AmazonS3 client = createClient(item.getBucketName());
		if (!item.isContainer()) {
			client.deleteObject(item.getBucketName(), item.getKey());
		} else {
			listObjects(client, item, result -> {
				result.getObjectSummaries().stream()
						.forEach(s -> client.deleteObject(item.getBucketName(), s.getKey()));
			});
		}
	}

	@Override
	public S3Folder createFolder(final String bucketName, final String key) {
		if (!key.endsWith(DELIMITER)) {
			throw new IllegalArgumentException("The key must be end with \"/\".");
		}
		final AmazonS3 client = createClient(bucketName);
		final ByteArrayInputStream emptyInputStream = new ByteArrayInputStream(new byte[0]);
		final ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0);
		client.putObject(bucketName, key, emptyInputStream, metadata);
		final ObjectListing listObjects = client.listObjects(bucketName, key);
		return listObjects.getObjectSummaries().stream().filter(s -> !isObject(s))
				.map(s -> new S3Folder(s.getBucketName(), s.getKey())).findFirst().get();
	}

	@Override
	public AsyncResult downloadObject(final S3Item item, final Path destination) throws IOException {
		final Supplier<ContentInfo> contentSupplier = () -> {
			final AmazonS3 client = createClient(item.getBucketName());
			final GetObjectRequest request = new GetObjectRequest(item.getBucketName(), item.getKey());
			final com.amazonaws.services.s3.model.S3Object object = client.getObject(request);
			final long contentLength = object.getObjectMetadata().getContentLength();
			final S3ObjectInputStream inputStream = object.getObjectContent();
			return new ContentInfo(contentLength, inputStream);
		};
		return IOUtil.downloadContent(contentSupplier, destination);
	}

	@Override
	public UploadObjectResult uploadObject(final String bucketName, final String key, final Path path)
			throws IOException {
		if (!Files.exists(path)) {
			throw new IllegalArgumentException("File does not exist.");
		}
		final AmazonS3 client = createClient(bucketName);
		final ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(Files.size(path));
		metadata.setContentType(URLConnection.guessContentTypeFromName(path.toString()));

		final UploadObjectResult result = new UploadObjectResult();
		result.setName(path.getFileName().toString());
		result.setTotal(metadata.getContentLength());

		ConcurrentUtil.run(() -> {
			try (InputStream input = new BufferedInputStream(
					new MonitorInputStream(Files.newInputStream(path, READ), result::addCurrent))) {
				client.putObject(bucketName, key, input, metadata);
				listObjects(client, bucketName, key, l -> {
					final S3Object object = l.getObjectSummaries().stream().filter(this::isObject).map(S3Object::new)
							.findFirst().get();
					result.setItem(object);
				});
			} catch (final Throwable e) {
				result.setThrown(e);
			}
		});
		return result;
	}

	@Override
	public S3Item copyObject(final String sourceBucketName, final String sourceKey, final String destinationBucketName,
			final String destinationKey) {
		final AmazonS3 srcClient = createClient(sourceBucketName);
		final AmazonS3 destClient = createClient(destinationBucketName);
		listObjects(srcClient, sourceBucketName, sourceKey, result -> {
			result.getObjectSummaries().stream().forEach(s -> {
				final String key = s.getKey().replaceAll(sourceKey, destinationKey);
				final CopyObjectRequest request = new CopyObjectRequest(s.getBucketName(), s.getKey(),
						destinationBucketName, key);
				destClient.copyObject(request);
			});
		});
		final ListObjectsRequest request = new ListObjectsRequest().withBucketName(destinationBucketName)
				.withPrefix(destinationKey).withDelimiter(S3Item.DELIMITER).withMaxKeys(1);
		final ObjectListing listObjects = destClient.listObjects(request);
		final S3Item item = listObjects.getObjectSummaries().stream().filter(s -> s.getKey().equals(destinationKey))
				.map(s -> isObject(s) ? new S3Object(s) : new S3Folder(s.getBucketName(), s.getKey())).findFirst()
				.get();
		if (item.isContainer()) {
			updateItems(item);
		}
		return item;
	}

	@Override
	public S3Item moveObject(final String sourceBucketName, final String sourceKey, final String destinationBucketName,
			final String destinationKey) {
		final S3Item item = copyObject(sourceBucketName, sourceKey, destinationBucketName, destinationKey);
		final AmazonS3 srcClient = createClient(sourceBucketName);
		listObjects(srcClient, sourceBucketName, sourceKey, result -> {
			result.getObjectSummaries().stream().forEach(s -> srcClient.deleteObject(s.getBucketName(), s.getKey()));
		});
		return item;
	}

	@Override
	public void publishObject(final S3Item item) {
		final AmazonS3 client = createClient(item.getBucketName());
		if (item.getType() == S3Item.Type.Bucket) {
			client.setBucketAcl(item.getBucketName(), PublicRead);
		} else if (item.isContainer()) {
			listObjects(client, item, l -> {
				l.getObjectSummaries().forEach(s -> client.setObjectAcl(s.getBucketName(), s.getKey(), PublicRead));
			});
		} else {
			client.setObjectAcl(item.getBucketName(), item.getKey(), PublicRead);
		}
	}

	private AmazonS3 createClient(final String bucketName) {
		final AmazonS3 client = createDefaultClient();
		final Region region = Region.fromValue(client.getBucketLocation(bucketName));
		final Regions regions = Regions.fromName(region.toAWSRegion().getName());
		if (regions == AP_NORTHEAST_1) {
			return client;
		}
		return createClient(regions);
	}

	private AmazonS3 createClient(final Regions regions) {
		final AWSCredentialsProvider provider = Credential.get().createCredentialsProvider();
		return AmazonS3ClientBuilder.standard().withCredentials(provider).withRegion(regions).build();
	}

	private AmazonS3 createDefaultClient() {
		return createClient(AP_NORTHEAST_1);
	}

	private void updateBuckets(final S3Root item) {
		final AmazonS3 client = createDefaultClient();
		final List<S3Bucket> buckets = client.listBuckets().stream().map(S3Bucket::new).collect(Collectors.toList());
		item.getItems().setAll(buckets);
		buckets.stream().forEach(i -> ConcurrentUtil.run(() -> updateItems(i)));
	}

	private void updateObjects(final S3Item item) {
		final List<S3Item> objects = new ArrayList<>();
		final List<String> folders = new ArrayList<>();
		listObjects(item, result -> {
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
		folders.stream().filter(f -> !structured.containsKey(f)).map(f -> new S3Folder(rootItem.getBucketName(), f))
				.peek(f -> structured.put(f.getKey(), f)).forEach(f -> storeInParent(structured, f));
		objects.forEach(i -> storeInParent(structured, i));
	}

	private void storeInParent(final Map<String, S3Item> structured, final S3Item item) {
		final String parentKey = item.getParentKey();
		if (structured.containsKey(parentKey)) {
			structured.get(parentKey).getItems().add(item);
		} else {
			final S3Virtual parent = new S3Virtual(item.getBucketName(), parentKey);
			parent.getItems().add(item);
			storeInParent(structured, parent);
			structured.put(parentKey, parent);
		}
	}

	private void listObjects(final S3Item item, final Consumer<ObjectListing> consumer) {
		final AmazonS3 client = createClient(item.getBucketName());
		listObjects(client, item, consumer);
	}

	private void listObjects(final AmazonS3 client, final S3Item item, final Consumer<ObjectListing> consumer) {
		listObjects(client, createListObjectsRequest(item), consumer);
	}

	private void listObjects(final AmazonS3 client, final String bucketName, final String key,
			final Consumer<ObjectListing> consumer) {
		final ListObjectsRequest request = new ListObjectsRequest().withBucketName(bucketName).withPrefix(key);
		listObjects(client, request, consumer);
	}

	private void listObjects(final AmazonS3 client, final ListObjectsRequest request,
			final Consumer<ObjectListing> consumer) {
		if (isShuttingDown()) {
			return;
		}
		ObjectListing result = null;
		do {
			result = client.listObjects(request);
			consumer.accept(result);
			request.setMarker(result.getNextMarker());
		} while (!isShuttingDown() && result.isTruncated());
	}

	class MonitorInputStream extends FilterInputStream {

		private final IntConsumer consumer;

		protected MonitorInputStream(final InputStream in, final IntConsumer consumer) {
			super(in);
			this.consumer = consumer;
		}

		@Override
		public int read(final byte[] b, final int off, final int len) throws IOException {
			final int read = super.read(b, off, len);
			consumer.accept(read);
			return read;
		}

	}
}
