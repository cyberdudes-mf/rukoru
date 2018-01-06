package hoshisugi.rukoru.framework.inject;

import hoshisugi.rukoru.framework.database.Database;
import hoshisugi.rukoru.framework.database.DatabaseImpl;

public class BasicModuleConfigurator extends ModuleConfigurator {

	@Override
	protected void configure() {
		provide(Database.class, new DatabaseImpl());
	}

}
