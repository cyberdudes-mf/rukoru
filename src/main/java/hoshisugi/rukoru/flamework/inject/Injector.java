package hoshisugi.rukoru.flamework.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;

public class Injector extends AbstractModule {

	private static final Injector INSTANCE = new Injector();

	private final com.google.inject.Injector injector;

	private Injector() {
		injector = Guice.createInjector(this);
	}

	public static void injectMembers(final Injectable instance) {
		INSTANCE.injector.injectMembers(instance);
	}

	@Override
	protected void configure() {

	}

}
