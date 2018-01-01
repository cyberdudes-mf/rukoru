package hoshisugi.rukoru.app.services.ec2;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.CreateImageRequest;
import com.amazonaws.services.ec2.model.CreateImageResult;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DeregisterImageRequest;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.ResourceType;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TagSpecification;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesResult;

import hoshisugi.rukoru.app.models.AuthSetting;
import hoshisugi.rukoru.app.models.CreateInstanceRequest;
import hoshisugi.rukoru.app.models.CreateMachineImageRequest;
import hoshisugi.rukoru.app.models.EC2Instance;
import hoshisugi.rukoru.app.models.MachineImage;
import hoshisugi.rukoru.flamework.services.BaseService;
import javafx.application.Platform;

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
	public List<MachineImage> listImages(final AuthSetting authSetting) {
		final AmazonEC2 client = createClient(authSetting);
		final DescribeImagesRequest request = new DescribeImagesRequest().withFilters(SPIDER_INSTANCE);
		final DescribeImagesResult result = client.describeImages(request);
		return result.getImages().stream().map(MachineImage::new)
				.sorted(Comparator.comparing(MachineImage::getCreationDate).reversed()).collect(Collectors.toList());
	}

	@Override
	public void updateTags(final AuthSetting authSetting, final EC2Instance instance, final Map<String, String> tags) {
		final AmazonEC2 client = createClient(authSetting);
		final List<Tag> newTags = tags.entrySet().stream().map(e -> new Tag(e.getKey(), e.getValue()))
				.collect(Collectors.toList());
		final CreateTagsRequest request = new CreateTagsRequest(Arrays.asList(instance.getInstanceId()), newTags);
		client.createTags(request);
	}

	@Override
	public void startInstance(final AuthSetting authSetting, final EC2Instance instance) {
		final AmazonEC2 client = createClient(authSetting);
		final StartInstancesRequest request = new StartInstancesRequest().withInstanceIds(instance.getInstanceId());
		final StartInstancesResult result = client.startInstances(request);
		Platform.runLater(() -> {
			result.getStartingInstances().stream().filter(s -> s.getInstanceId().equals(instance.getInstanceId()))
					.findFirst().ifPresent(s -> instance.setState(s.getCurrentState().getName()));
		});
		final DescribeInstancesRequest describeRequest = createDescribeRequestById(instance);
		while (!instance.getState().equals("running")) {
			final DescribeInstancesResult describeResult = client.describeInstances(describeRequest);
			Platform.runLater(() -> {
				describeResult.getReservations().stream().flatMap(r -> r.getInstances().stream())
						.filter(i -> i.getInstanceId().equals(instance.getInstanceId())).findFirst().ifPresent(i -> {
							instance.setState(i.getState().getName());
							instance.setPublicIpAddress(i.getPublicIpAddress());
						});
			});
			sleep(3000);
		}
	}

	@Override
	public void stopInstance(final AuthSetting authSetting, final EC2Instance instance) {
		final AmazonEC2 client = createClient(authSetting);
		final StopInstancesRequest request = new StopInstancesRequest().withInstanceIds(instance.getInstanceId());
		final StopInstancesResult result = client.stopInstances(request);
		Platform.runLater(() -> {
			result.getStoppingInstances().stream().filter(s -> s.getInstanceId().equals(instance.getInstanceId()))
					.findFirst().ifPresent(s -> instance.setState(s.getCurrentState().getName()));
		});
		final DescribeInstancesRequest describeRequest = createDescribeRequestById(instance);
		while (!instance.getState().equals("stopped")) {
			final DescribeInstancesResult describeResult = client.describeInstances(describeRequest);
			Platform.runLater(() -> {
				describeResult.getReservations().stream().flatMap(r -> r.getInstances().stream())
						.filter(i -> i.getInstanceId().equals(instance.getInstanceId())).findFirst().ifPresent(i -> {
							instance.setState(i.getState().getName());
							instance.setPublicIpAddress(i.getPublicIpAddress());
						});
			});
			sleep(3000);
		}
	}

	@Override
	public List<EC2Instance> createInstance(final AuthSetting authSetting,
			final CreateInstanceRequest createInstanceRequest) {
		final AmazonEC2 client = createClient(authSetting);
		final RunInstancesRequest request = createRunInstanceRequest(createInstanceRequest);
		final RunInstancesResult result = client.runInstances(request);
		return result.getReservation().getInstances().stream().map(EC2Instance::new).collect(Collectors.toList());
	}

	@Override
	public void terminateInstance(final AuthSetting authSetting, final EC2Instance instance) {
		final AmazonEC2 client = createClient(authSetting);
		final TerminateInstancesRequest request = new TerminateInstancesRequest()
				.withInstanceIds(instance.getInstanceId());
		final TerminateInstancesResult result = client.terminateInstances(request);
		Platform.runLater(() -> {
			result.getTerminatingInstances().stream().filter(i -> i.getInstanceId().equals(instance.getInstanceId()))
					.forEach(i -> instance.setState(i.getCurrentState().getName()));
		});
	}

	@Override
	public void createMachineImage(final AuthSetting authSetting, final CreateMachineImageRequest createImageRequest) {
		final AmazonEC2 client = createClient(authSetting);
		final CreateImageRequest request = new CreateImageRequest();
		request.setInstanceId(createImageRequest.getInstanceId());
		request.setName(createImageRequest.getName());
		request.setDescription(createImageRequest.getDescription());
		request.setNoReboot(createImageRequest.isNoReboot());
		final CreateImageResult result = client.createImage(request);

		// タグを付ける
		final CreateTagsRequest tagRequest = new CreateTagsRequest().withResources(result.getImageId());
		final List<Tag> tags = createImageRequest.getTags().stream().map(t -> new Tag(t.getKey(), t.getValue()))
				.collect(Collectors.toList());
		tagRequest.setTags(tags);
		client.createTags(tagRequest);
	}

	@Override
	public void deregisterImage(final AuthSetting authSetting, final MachineImage image) {
		final AmazonEC2 client = createClient(authSetting);
		final DeregisterImageRequest request = new DeregisterImageRequest(image.getImageId());
		client.deregisterImage(request);
	}

	private AmazonEC2 createClient(final AuthSetting authSetting) {
		return AmazonEC2ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(
						new BasicAWSCredentials(authSetting.getAccessKeyId(), authSetting.getSecretAccessKey())))
				.withRegion(Regions.AP_NORTHEAST_1).build();
	}

	private DescribeInstancesRequest createDescribeRequestById(final EC2Instance instance) {
		return new DescribeInstancesRequest().withInstanceIds(instance.getInstanceId());
	}

	private void sleep(final int millis) {
		try {
			Thread.sleep(millis);
		} catch (final InterruptedException e) {
		}
	}

	private RunInstancesRequest createRunInstanceRequest(final CreateInstanceRequest createInstanceRequest) {
		final RunInstancesRequest request = new RunInstancesRequest();
		request.setImageId(createInstanceRequest.getImageId());
		request.setInstanceType(createInstanceRequest.getInstanceType().getValue());
		request.setMinCount(createInstanceRequest.getMinCount());
		request.setMaxCount(createInstanceRequest.getMaxCount());
		request.setKeyName(createInstanceRequest.getKeyName());
		request.withSecurityGroups(createInstanceRequest.getSecurityGroup());
		final List<Tag> tags = createInstanceRequest.getTags().stream().filter(Objects::nonNull)
				.map(t -> new Tag(t.getKey(), t.getValue())).collect(Collectors.toList());
		final TagSpecification instanceTag = new TagSpecification().withResourceType(ResourceType.Instance)
				.withTags(tags);
		final TagSpecification volumeTag = new TagSpecification().withResourceType(ResourceType.Volume).withTags(tags);
		request.withTagSpecifications(instanceTag, volumeTag);
		return request;
	}

}
