package hoshisugi.rukoru.app.services.s3;

import static com.amazonaws.regions.Regions.AP_NORTHEAST_1;
import static hoshisugi.rukoru.app.models.s3.S3Item.DELIMITER;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.common.base.Strings;

import hoshisugi.rukoru.app.models.auth.AuthSetting;
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
		final AmazonS3 client = createClient();
		final ListObjectsRequest request = createListObjectsRequest(item, item.getBucketName());
		final List<S3Item> objects = new ArrayList<>();
		ObjectListing result = null;
		do {
			result = client.listObjects(request);
			result.getObjectSummaries().stream().filter(this::isObject).map(S3Object::new).forEach(objects::add);
			request.setMarker(result.getNextMarker());
		} while (result.isTruncated());
		toTreeStructure(item, objects);
	}

	private ListObjectsRequest createListObjectsRequest(final S3Item item, final String bucketName) {
		final ListObjectsRequest request = new ListObjectsRequest();
		request.setBucketName(bucketName);
		final String key = item.getKey();
		if (!Strings.isNullOrEmpty(key)) {
			request.setPrefix(item.getKey());
			request.setDelimiter(DELIMITER);
		}
		return request;
	}

	private boolean isObject(final S3ObjectSummary object) {
		return !object.getKey().endsWith(DELIMITER);
	}

	private void toTreeStructure(final S3Item rootItem, final List<S3Item> objects) {
		final Map<String, S3Item> structured = new HashMap<>();
		structured.put(rootItem.getKey(), rootItem);
		rootItem.getItems().clear();
		objects.forEach(i -> storeInParent(structured, i));
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
}
