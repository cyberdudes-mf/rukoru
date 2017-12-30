package hoshisugi.rukoru.app.services.ec2;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Filter;

import hoshisugi.rukoru.app.models.AMI;
import hoshisugi.rukoru.app.models.AuthSetting;
import hoshisugi.rukoru.app.models.EC2Instance;
import hoshisugi.rukoru.flamework.services.BaseService;

public class EC2ServiceImpl extends BaseService implements EC2Service {

	private static final Filter SPIDER_INSTANCE = new Filter("tag-key", Arrays.asList("SpiderInstance"));

	@Override
	public List<EC2Instance> listInstances(final AuthSetting authSetting) {
		final AmazonEC2 client = createClient(authSetting);
		final DescribeInstancesRequest request = new DescribeInstancesRequest().withFilters(SPIDER_INSTANCE);
		final DescribeInstancesResult result = client.describeInstances(request);
		return result.getReservations().stream().flatMap(r -> r.getInstances().stream()).map(EC2Instance::new)
				.sorted(Comparator.comparing(EC2Instance::getLaunchTime).reversed()).collect(Collectors.toList());
	}

	@Override
	public List<AMI> listImages(final AuthSetting authSetting) {
		final AmazonEC2 client = createClient(authSetting);
		final DescribeImagesRequest request = new DescribeImagesRequest().withFilters(SPIDER_INSTANCE);
		final DescribeImagesResult result = client.describeImages(request);
		return result.getImages().stream().map(AMI::new).sorted(Comparator.comparing(AMI::getCreationDate).reversed())
				.collect(Collectors.toList());
	}

	private AmazonEC2 createClient(final AuthSetting authSetting) {
		return AmazonEC2ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(
						new BasicAWSCredentials(authSetting.getAccessKeyId(), authSetting.getSecretAccessKey())))
				.withRegion(Regions.AP_NORTHEAST_1).build();
	}

}
