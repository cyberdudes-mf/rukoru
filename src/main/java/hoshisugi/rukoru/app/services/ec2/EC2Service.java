package hoshisugi.rukoru.app.services.ec2;

import static hoshisugi.rukoru.app.enums.EC2InstanceState.Running;
import static hoshisugi.rukoru.app.enums.EC2InstanceState.Stopped;
import static hoshisugi.rukoru.app.enums.EC2InstanceState.Terminated;

import java.util.List;
import java.util.Map;

import com.amazonaws.services.ec2.model.ImageState;

import hoshisugi.rukoru.app.enums.EC2InstanceState;
import hoshisugi.rukoru.app.models.ec2.CreateInstanceRequest;
import hoshisugi.rukoru.app.models.ec2.CreateMachineImageRequest;
import hoshisugi.rukoru.app.models.ec2.EC2Instance;
import hoshisugi.rukoru.app.models.ec2.MachineImage;

public interface EC2Service {

	List<EC2Instance> listInstances();

	List<MachineImage> listImages();

	void updateTags(EC2Instance instance, Map<String, String> tags);

	void startInstance(EC2Instance instance);

	void stopInstance(EC2Instance instance);

	List<EC2Instance> createInstance(CreateInstanceRequest request);

	void terminateInstance(EC2Instance instance);

	List<MachineImage> createMachineImage(CreateMachineImageRequest request);

	void deregisterMachineImage(MachineImage image);

	void monitorInstances(List<EC2Instance> instances);

	void monitorImages(List<MachineImage> images);

	static boolean needMonitoring(final EC2Instance instance) {
		final EC2InstanceState state = EC2InstanceState.of(instance.getState());
		return state != Running && state != Stopped && state != Terminated;
	}

	static boolean needMonitoring(final MachineImage image) {
		return !ImageState.Available.toString().equals(image.getState());
	}

	static boolean noNeedMonitoring(final EC2Instance instance) {
		return !needMonitoring(instance);
	}

	static boolean noNeedMonitoring(final MachineImage image) {
		return !needMonitoring(image);
	}
}
