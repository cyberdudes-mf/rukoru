package hoshisugi.rukoru.app.services.ec2;

import static com.amazonaws.regions.Regions.AP_NORTHEAST_1;

import java.util.List;
import java.util.stream.Collectors;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;

import hoshisugi.rukoru.app.models.AuthSetting;
import hoshisugi.rukoru.app.models.S3Bucket;
import hoshisugi.rukoru.flamework.services.BaseService;

public class S3ServiceImpl extends BaseService implements S3Service {

	@Override
	public List<S3Bucket> listBuckets() {
		final AmazonS3 client = createClient();
		final List<Bucket> buckets = client.listBuckets();
		return buckets.stream().map(S3Bucket::new).collect(Collectors.toList());
	}

	private AmazonS3 createClient() {
		final AuthSetting authSetting = AuthSetting.get();
		final AWSCredentials credential = new BasicAWSCredentials(authSetting.getAccessKeyId(),
				authSetting.getSecretAccessKey());
		final AWSCredentialsProvider provider = new AWSStaticCredentialsProvider(credential);
		return AmazonS3ClientBuilder.standard().withCredentials(provider).withRegion(AP_NORTHEAST_1).build();
	}

}
