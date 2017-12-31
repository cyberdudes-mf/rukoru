package hoshisugi.rukoru.app.inject;

import com.google.inject.Singleton;

import hoshisugi.rukoru.app.services.auth.AuthService;
import hoshisugi.rukoru.app.services.auth.AuthServiceImpl;
import hoshisugi.rukoru.app.services.ec2.EC2Service;
import hoshisugi.rukoru.app.services.ec2.EC2ServiceImpl;
import hoshisugi.rukoru.app.view.ContentController;
import hoshisugi.rukoru.app.view.MainController;
import hoshisugi.rukoru.app.view.ToolBarController;
import hoshisugi.rukoru.app.view.content.AMITabController;
import hoshisugi.rukoru.app.view.content.EC2ContentController;
import hoshisugi.rukoru.app.view.content.EC2InstanceTabController;
import hoshisugi.rukoru.app.view.content.RepositoryDBContentController;
import hoshisugi.rukoru.app.view.content.S3ContentController;
import hoshisugi.rukoru.app.view.popup.AuthSettingController;
import hoshisugi.rukoru.app.view.popup.CreateInstanceController;
import hoshisugi.rukoru.flamework.inject.ModuleConfigurator;

public class RukoruModuleConfigurator extends ModuleConfigurator {

	@Override
	protected void configure() {
		configureServices();
		configureControllers();
	}

	private void configureControllers() {
		provide(MainController.class);
		provide(ContentController.class);
		provide(ToolBarController.class);
		provide(EC2ContentController.class);
		provide(RepositoryDBContentController.class);
		provide(S3ContentController.class);
		provide(EC2InstanceTabController.class);
		provide(AMITabController.class);
		provide(AuthSettingController.class);
		provide(CreateInstanceController.class);
	}

	private void configureServices() {
		bind(AuthService.class).toProvider(() -> new AuthServiceImpl()).in(Singleton.class);
		bind(EC2Service.class).toProvider(() -> new EC2ServiceImpl()).in(Singleton.class);
	}
}
