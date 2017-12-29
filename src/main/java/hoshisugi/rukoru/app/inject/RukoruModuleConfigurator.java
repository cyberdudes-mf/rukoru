package hoshisugi.rukoru.app.inject;

import com.google.inject.Singleton;

import hoshisugi.rukoru.app.services.AuthService;
import hoshisugi.rukoru.app.services.AuthServiceImpl;
import hoshisugi.rukoru.app.view.ConsoleController;
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
		provide(ConsoleController.class);
		provide(ToolBarController.class);
	}

	private void configureServices() {
		bind(AuthService.class).toProvider(() -> new AuthServiceImpl()).in(Singleton.class);
	}
}
