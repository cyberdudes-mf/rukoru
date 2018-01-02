package hoshisugi.rukoru.app.services.s3;

import static com.amazonaws.regions.Regions.AP_NORTHEAST_1;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.common.base.Strings;

import hoshisugi.rukoru.app.models.AuthSetting;
import hoshisugi.rukoru.app.models.S3Bucket;
import hoshisugi.rukoru.app.models.S3Folder;
import hoshisugi.rukoru.app.models.S3Item;
import hoshisugi.rukoru.app.models.S3Object;
import hoshisugi.rukoru.flamework.services.BaseService;
import javafx.collections.ObservableList;

public class S3ServiceImpl extends BaseService implements S3Service {

	private AmazonS3 createClient() {
		final AuthSetting authSetting = AuthSetting.get();
		final AWSCredentials credential = new BasicAWSCredentials(authSetting.getAccessKeyId(),
				authSetting.getSecretAccessKey());
		final AWSCredentialsProvider provider = new AWSStaticCredentialsProvider(credential);
		return AmazonS3ClientBuilder.standard().withCredentials(provider).withRegion(AP_NORTHEAST_1).build();
	}

	private List<S3Item> listBuckets() {
		final AmazonS3 client = createClient();
		final List<Bucket> buckets = client.listBuckets();
		return buckets.stream().map(S3Bucket::new).collect(Collectors.toList());
	}

	@Override
	public void updateItems(final S3Item item) {
		final ObservableList<S3Item> items = item.getItems();
		if (Strings.isNullOrEmpty(item.getBucketName())) {
			items.setAll(listBuckets());
			return;
		}
		final AmazonS3 client = createClient();

		final String bucketName = item.getBucketName();
		final ListObjectsRequest request = new ListObjectsRequest();
		request.setBucketName(bucketName);
		request.setPrefix(item.getKey());
		request.setDelimiter("/");

		final List<S3Folder> folders = new ArrayList<>();
		final List<S3Object> files = new ArrayList<>();

		ObjectListing result = null;
		do {
			result = client.listObjects(request);
			result.getCommonPrefixes().stream().map(s -> new S3Folder(bucketName, s)).forEach(folders::add);
			result.getObjectSummaries().stream().filter(this::isObject).map(S3Object::new).forEach(files::add);
			request.setMarker(result.getNextMarker());
		} while (result.isTruncated());

		items.clear();
		items.addAll(folders);
		items.addAll(files);
	}

	private boolean isObject(final S3ObjectSummary summary) {
		return !summary.getKey().endsWith("/");
	}
}
