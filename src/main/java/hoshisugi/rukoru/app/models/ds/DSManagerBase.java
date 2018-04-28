package hoshisugi.rukoru.app.models.ds;

import hoshisugi.rukoru.app.services.ds.DSService;
import hoshisugi.rukoru.framework.inject.Injector;

public abstract class DSManagerBase implements DSManager {

	protected final DSService service;

	public DSManagerBase() {
		service = Injector.getInstance(DSService.class);
	}

}
