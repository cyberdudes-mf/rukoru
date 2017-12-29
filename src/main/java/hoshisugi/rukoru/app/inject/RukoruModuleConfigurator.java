package hoshisugi.rukoru.app.inject;

import hoshisugi.rukoru.app.view.ConsoleController;
import hoshisugi.rukoru.app.view.MainController;
import hoshisugi.rukoru.app.view.ToolBarController;
import hoshisugi.rukoru.flamework.inject.ModuleConfigurator;

public class RukoruModuleConfigurator extends ModuleConfigurator {

	@Override
	protected void configure() {
		provide(MainController.class);
		provide(ConsoleController.class);
		provide(ToolBarController.class);
	}

}
