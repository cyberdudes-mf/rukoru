package hoshisugi.rukoru.flamework.inject;

import hoshisugi.rukoru.flamework.database.Database;
import hoshisugi.rukoru.flamework.database.DatabaseImpl;

public class BasicModuleConfigurator extends ModuleConfigurator {

	@Override
	protected void configure() {
		provide(Database.class, new DatabaseImpl());
	}

}
