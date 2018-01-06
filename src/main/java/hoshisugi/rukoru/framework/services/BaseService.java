package hoshisugi.rukoru.framework.services;

import hoshisugi.rukoru.framework.inject.Injectable;
import hoshisugi.rukoru.framework.inject.Injector;

public abstract class BaseService implements Injectable {

	public BaseService() {
		Injector.injectMembers(this);
	}

}
