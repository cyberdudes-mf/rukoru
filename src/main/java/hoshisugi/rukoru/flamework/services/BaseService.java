package hoshisugi.rukoru.flamework.services;

import hoshisugi.rukoru.flamework.inject.Injectable;
import hoshisugi.rukoru.flamework.inject.Injector;

public abstract class BaseService implements Injectable {

	public BaseService() {
		Injector.injectMembers(this);
	}

}
