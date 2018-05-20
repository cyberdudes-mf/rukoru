package hoshisugi.rukoru.app.services.video;

import static com.amazonaws.regions.Regions.AP_NORTHEAST_1;
import static hoshisugi.rukoru.framework.event.ShutdownHandler.isShuttingDown;

import java.net.URL;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import hoshisugi.rukoru.app.models.settings.S3VideoCredential;
import hoshisugi.rukoru.app.models.video.VideoContainer;
import hoshisugi.rukoru.app.models.video.VideoFile;
import hoshisugi.rukoru.app.models.video.VideoFolder;
import hoshisugi.rukoru.app.models.video.VideoItem;
import hoshisugi.rukoru.app.models.video.VideoRoot;
import hoshisugi.rukoru.framework.base.BaseService;
import hoshisugi.rukoru.framework.util.DateTimeUtil;

public class S3VideoServiceImpl extends BaseService implements VideoService {

	private AmazonS3 client;

	public S3VideoServiceImpl() {
		if (S3VideoCredential.hasCredential()) {
			createClient(S3VideoCredential.get());
		}
	}

	@Override
	public VideoItem getVideos() {
		final S3VideoCredential credential = S3VideoCredential.get();
		createClient(credential);

		final VideoItem root = new VideoRoot();
		final String bucketName = credential.getBucket();
		final Optional<Bucket> bucketOptional = getBucket(bucketName);
		if (!bucketOptional.isPresent()) {
			return root;
		}

		final Map<String, VideoItem> items = new HashMap<>();
		items.put("", root);

		final ListObjectsRequest request = new ListObjectsRequest().withBucketName(bucketName);
		ObjectListing result = null;
		do {
			result = client.listObjects(request);
			result.getObjectSummaries().stream().filter(s -> isFile(s.getKey())).forEach(s -> {
				final String key = s.getKey();
				final VideoFile value = new VideoFile(getBaseName(key), generateUrl(s));
				putItem(items, key, value);
			});
			request.setMarker(result.getNextMarker());
		} while (!isShuttingDown() && result.isTruncated());
		return root;
	}

	private void createClient(final S3VideoCredential credential) {
		final BasicAWSCredentials credentials = new BasicAWSCredentials(credential.getAccessKeyId(),
				credential.getSecretAccessKey());
		final AWSCredentialsProvider provider = new AWSStaticCredentialsProvider(credentials);
		client = AmazonS3ClientBuilder.standard().withCredentials(provider).withPathStyleAccessEnabled(true)
				.withRegion(AP_NORTHEAST_1).build();
	}

	private Optional<Bucket> getBucket(final String bucketName) {
		return client.listBuckets().stream().filter(b -> b.getName().equals(bucketName)).findFirst();
	}

	private URL generateUrl(final S3ObjectSummary summary) {
		final GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(summary.getBucketName(),
				summary.getKey());
		request.setExpiration(DateTimeUtil.toDate(ZonedDateTime.now().plusHours(3)));
		return client.generatePresignedUrl(request);
	}

	private boolean isFile(final String key) {
		return !key.endsWith("/");
	}

	private void putItem(final Map<String, VideoItem> items, final String key, final VideoItem item) {
		final String parentKey = getParentKey(key);
		final VideoContainer folder;
		if (items.containsKey(parentKey)) {
			folder = (VideoContainer) items.get(parentKey);
		} else {
			folder = new VideoFolder(getBaseName(parentKey));
			items.put(parentKey, folder);
			putItem(items, parentKey, folder);
		}
		folder.add(item);
	}

	private String getParentKey(final String key) {
		if (isFile(key)) {
			return key.substring(0, key.lastIndexOf("/") + 1);
		} else {
			return key.substring(0, key.lastIndexOf("/", key.length() - 2) + 1);
		}
	}

	private String getBaseName(final String key) {
		if (isFile(key)) {
			return key.substring(key.lastIndexOf("/") + 1);
		} else {
			return key.substring(key.lastIndexOf("/", key.length() - 2) + 1, key.lastIndexOf("/"));
		}
	}
}
