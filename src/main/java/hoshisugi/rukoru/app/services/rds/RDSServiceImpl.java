package hoshisugi.rukoru.app.services.rds;

import static com.amazonaws.regions.Regions.AP_NORTHEAST_1;

import java.util.List;
import java.util.stream.Collectors;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.AmazonRDSClientBuilder;
import com.amazonaws.services.rds.model.DescribeDBInstancesResult;

import hoshisugi.rukoru.app.models.auth.AuthSetting;
import hoshisugi.rukoru.app.models.rds.RDSInstance;
import hoshisugi.rukoru.framework.base.BaseService;

public class RDSServiceImpl extends BaseService implements RDSService {

	@Override
	public List<RDSInstance> listInstances() {
		final AmazonRDS client = createClient();
		final DescribeDBInstancesResult result = client.describeDBInstances();
		return result.getDBInstances().stream().map(RDSInstance::new).collect(Collectors.toList());
	}

	private AmazonRDS createClient() {
		final AuthSetting authSetting = AuthSetting.get();
		final AWSCredentials credential = new BasicAWSCredentials(authSetting.getAccessKeyId(),
				authSetting.getSecretAccessKey());
		final AWSCredentialsProvider provider = new AWSStaticCredentialsProvider(credential);
		return AmazonRDSClientBuilder.standard().withCredentials(provider).withRegion(AP_NORTHEAST_1).build();
	}

}
