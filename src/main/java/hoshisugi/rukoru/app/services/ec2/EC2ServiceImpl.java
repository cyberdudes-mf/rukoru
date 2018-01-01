package hoshisugi.rukoru.app.services.ec2;

import static com.amazonaws.regions.Regions.AP_NORTHEAST_1;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
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
	public List<EC2Instance> listInstances() {
		final AmazonEC2 client = createClient();
		final DescribeInstancesRequest request = new DescribeInstancesRequest().withFilters(SPIDER_INSTANCE);
		final DescribeInstancesResult result = client.describeInstances(request);
		return result.getReservations().stream().flatMap(r -> r.getInstances().stream()).map(EC2Instance::new)
				.sorted(Comparator.comparing(EC2Instance::getLaunchTime).reversed()).collect(Collectors.toList());
	}

	@Override
	public List<MachineImage> listImages() {
		final AmazonEC2 client = createClient();
		final DescribeImagesRequest request = new DescribeImagesRequest().withFilters(SPIDER_INSTANCE);
		final DescribeImagesResult result = client.describeImages(request);
		return result.getImages().stream().map(MachineImage::new)
				.sorted(Comparator.comparing(MachineImage::getCreationDate).reversed()).collect(Collectors.toList());
	}

	@Override
	public void updateTags(final EC2Instance instance, final Map<String, String> tags) {
		final AmazonEC2 client = createClient();
		final List<Tag> newTags = tags.entrySet().stream().map(e -> new Tag(e.getKey(), e.getValue()))
				.collect(Collectors.toList());
		final CreateTagsRequest request = new CreateTagsRequest(Arrays.asList(instance.getInstanceId()), newTags);
		client.createTags(request);
	}

	@Override
	public void startInstance(final EC2Instance instance) {
		final AmazonEC2 client = createClient();
		final StartInstancesRequest request = new StartInstancesRequest().withInstanceIds(instance.getInstanceId());
		final StartInstancesResult result = client.startInstances(request);
		Platform.runLater(() -> {
			result.getStartingInstances().stream().filter(s -> s.getInstanceId().equals(instance.getInstanceId()))
					.findFirst().ifPresent(s -> instance.setState(s.getCurrentState().getName()));
		});
	}

	@Override
	public void stopInstance(final EC2Instance instance) {
		final AmazonEC2 client = createClient();
		final StopInstancesRequest request = new StopInstancesRequest().withInstanceIds(instance.getInstanceId());
		final StopInstancesResult result = client.stopInstances(request);
		Platform.runLater(() -> {
			result.getStoppingInstances().stream().filter(s -> s.getInstanceId().equals(instance.getInstanceId()))
					.findFirst().ifPresent(s -> instance.setState(s.getCurrentState().getName()));
		});
	}

	@Override
	public List<EC2Instance> createInstance(final CreateInstanceRequest createInstanceRequest) {
		final AmazonEC2 client = createClient();
		final RunInstancesRequest request = createRunInstanceRequest(createInstanceRequest);
		final RunInstancesResult result = client.runInstances(request);
		return result.getReservation().getInstances().stream().map(EC2Instance::new).collect(Collectors.toList());
	}

	@Override
	public void terminateInstance(final EC2Instance instance) {
		final AmazonEC2 client = createClient();
		final TerminateInstancesRequest request = new TerminateInstancesRequest()
				.withInstanceIds(instance.getInstanceId());
		final TerminateInstancesResult result = client.terminateInstances(request);
		Platform.runLater(() -> {
			result.getTerminatingInstances().stream().filter(i -> i.getInstanceId().equals(instance.getInstanceId()))
					.forEach(i -> instance.setState(i.getCurrentState().getName()));
		});
	}

	@Override
	public List<MachineImage> createMachineImage(final CreateMachineImageRequest createImageRequest) {
		final AmazonEC2 client = createClient();
		final CreateImageRequest request = new CreateImageRequest();
		request.setInstanceId(createImageRequest.getInstanceId());
		request.setName(createImageRequest.getName());
		request.setDescription(createImageRequest.getDescription());
		request.setNoReboot(createImageRequest.isNoReboot());
		final CreateImageResult result = client.createImage(request);

		final CreateTagsRequest tagRequest = new CreateTagsRequest().withResources(result.getImageId());
		final List<Tag> tags = createImageRequest.getTags().stream().map(t -> new Tag(t.getKey(), t.getValue()))
				.collect(Collectors.toList());
		tagRequest.setTags(tags);
		client.createTags(tagRequest);

		final DescribeImagesRequest describeRequest = new DescribeImagesRequest().withImageIds(result.getImageId());
		final DescribeImagesResult describeResult = client.describeImages(describeRequest);
		return describeResult.getImages().stream().map(MachineImage::new)
				.sorted(Comparator.comparing(MachineImage::getCreationDate).reversed()).collect(Collectors.toList());
	}

	@Override
	public void deregisterMachineImage(final MachineImage image) {
		final AmazonEC2 client = createClient();
		final DeregisterImageRequest request = new DeregisterImageRequest(image.getImageId());
		client.deregisterImage(request);
	}

	@Override
	public void monitorInstances(final List<EC2Instance> instances) {
		final Map<String, EC2Instance> instanceIdMap = instances.stream()
				.collect(Collectors.toMap(EC2Instance::getInstanceId, Function.identity()));
		final AmazonEC2 client = createClient();
		while (instanceIdMap.values().stream().anyMatch(EC2Service::needMonitoring)) {
			final DescribeInstancesRequest request = new DescribeInstancesRequest()
					.withInstanceIds(instanceIdMap.keySet());
			final DescribeInstancesResult result = client.describeInstances(request);
			Platform.runLater(() -> {
				result.getReservations().stream().flatMap(r -> r.getInstances().stream()).map(i -> {
					final EC2Instance target = instanceIdMap.get(i.getInstanceId());
					target.update(i);
					return target;
				}).filter(EC2Service::noNeedMonitoring).forEach(done -> instanceIdMap.remove(done.getInstanceId()));
			});
			sleep(5000);
		}
	}

	@Override
	public void monitorImages(final List<MachineImage> images) {
		final Map<String, MachineImage> imageIdMap = images.stream()
				.collect(Collectors.toMap(MachineImage::getImageId, Function.identity()));
		final AmazonEC2 client = createClient();
		while (imageIdMap.values().stream().anyMatch(EC2Service::needMonitoring)) {
			final DescribeImagesRequest request = new DescribeImagesRequest().withImageIds(imageIdMap.keySet());
			final DescribeImagesResult result = client.describeImages(request);
			Platform.runLater(() -> {
				result.getImages().stream().map(i -> {
					final MachineImage target = imageIdMap.get(i.getImageId());
					target.update(i);
					return target;
				}).filter(EC2Service::noNeedMonitoring).forEach(done -> imageIdMap.remove(done.getImageId()));
			});
			sleep(5000);
		}
	}

	private AmazonEC2 createClient() {
		final AuthSetting authSetting = AuthSetting.get();
		final AWSCredentials credential = new BasicAWSCredentials(authSetting.getAccessKeyId(),
				authSetting.getSecretAccessKey());
		final AWSCredentialsProvider provider = new AWSStaticCredentialsProvider(credential);
		return AmazonEC2ClientBuilder.standard().withCredentials(provider).withRegion(AP_NORTHEAST_1).build();
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
