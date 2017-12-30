package hoshisugi.rukoru.app.inject;

import com.google.inject.Singleton;

import hoshisugi.rukoru.app.services.auth.AuthService;
import hoshisugi.rukoru.app.services.auth.AuthServiceImpl;
import hoshisugi.rukoru.app.services.ec2.EC2Service;
import hoshisugi.rukoru.app.services.ec2.EC2ServiceImpl;
import hoshisugi.rukoru.app.view.ContentController;
import hoshisugi.rukoru.app.view.MainController;
import hoshisugi.rukoru.app.view.ToolBarController;
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
	}

	private void configureServices() {
		bind(AuthService.class).toProvider(() -> new AuthServiceImpl()).in(Singleton.class);
		bind(EC2Service.class).toProvider(() -> new EC2ServiceImpl()).in(Singleton.class);
	}
}
