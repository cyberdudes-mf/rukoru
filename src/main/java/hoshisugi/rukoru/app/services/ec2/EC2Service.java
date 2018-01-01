package hoshisugi.rukoru.app.services.ec2;

import java.util.List;
import java.util.Map;

import hoshisugi.rukoru.app.models.CreateInstanceRequest;
import hoshisugi.rukoru.app.models.CreateMachineImageRequest;
import hoshisugi.rukoru.app.models.EC2Instance;
import hoshisugi.rukoru.app.models.MachineImage;

public interface EC2Service {

	List<EC2Instance> listInstances();

	List<MachineImage> listImages();

	void updateTags(EC2Instance instance, Map<String, String> tags);

	void startInstance(EC2Instance instance);

	void stopInstance(EC2Instance instance);

	List<EC2Instance> createInstance(CreateInstanceRequest request);

	void terminateInstance(EC2Instance instance);

	void createMachineImage(CreateMachineImageRequest request);

	void deregisterImage(MachineImage image);
}
