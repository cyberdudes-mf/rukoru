package hoshisugi.rukoru.app.services.ec2;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;

import hoshisugi.rukoru.app.models.AuthSetting;
import hoshisugi.rukoru.app.models.EC2Instance;
import hoshisugi.rukoru.flamework.services.BaseService;

public class EC2ServiceImpl extends BaseService implements EC2Service {

	@Override
	public List<EC2Instance> listInstances(final AuthSetting authSetting) {
		final AmazonEC2 client = AmazonEC2ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(
						new BasicAWSCredentials(authSetting.getAccessKeyId(), authSetting.getSecretAccessKey())))
				.withRegion(Regions.AP_NORTHEAST_1).build();
		final DescribeInstancesResult result = client.describeInstances();
		return result.getReservations().stream().flatMap(r -> r.getInstances().stream())
				.filter(i -> i.getTags().stream().anyMatch(t -> t.getKey().equals("SpiderInstance")))
				.map(EC2Instance::new).sorted(Comparator.comparing(EC2Instance::getLaunchTime).reversed())
				.collect(Collectors.toList());
	}

}
