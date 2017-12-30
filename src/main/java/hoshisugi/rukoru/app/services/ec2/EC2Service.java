package hoshisugi.rukoru.app.services.ec2;

import java.util.List;

import hoshisugi.rukoru.app.models.AMI;
import hoshisugi.rukoru.app.models.AuthSetting;
import hoshisugi.rukoru.app.models.EC2Instance;

public interface EC2Service {

	List<EC2Instance> listInstances(AuthSetting authSetting);

	List<AMI> listImages(AuthSetting authSetting);
}
