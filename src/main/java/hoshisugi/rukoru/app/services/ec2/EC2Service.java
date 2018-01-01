package hoshisugi.rukoru.app.services.ec2;

import java.util.List;
import java.util.Map;

import hoshisugi.rukoru.app.models.AuthSetting;
import hoshisugi.rukoru.app.models.CreateInstanceRequest;
import hoshisugi.rukoru.app.models.CreateMachineImageRequest;
import hoshisugi.rukoru.app.models.EC2Instance;
import hoshisugi.rukoru.app.models.MachineImage;

public interface EC2Service {

	List<EC2Instance> listInstances(AuthSetting authSetting);

	List<MachineImage> listImages(AuthSetting authSetting);

	void updateTags(AuthSetting authSetting, EC2Instance instance, Map<String, String> tags);

	void startInstance(AuthSetting authSetting, EC2Instance instance);

	void stopInstance(AuthSetting authSetting, EC2Instance instance);

	List<EC2Instance> createInstance(AuthSetting authSetting, CreateInstanceRequest request);

	void terminateInstance(AuthSetting authSetting, EC2Instance instance);

	void createMachineImage(AuthSetting authSetting, CreateMachineImageRequest request);

	void deregisterImage(AuthSetting authSetting, MachineImage image);
}
