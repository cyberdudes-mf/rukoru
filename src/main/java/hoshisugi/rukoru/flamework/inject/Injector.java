package hoshisugi.rukoru.flamework.inject;

import com.google.inject.Guice;

public class Injector {

	private static Injector INSTANCE;

	private final ModuleConfigurator configurator;
	private final com.google.inject.Injector injector;

	public static void init(final ModuleConfigurator configurator) {
		INSTANCE = new Injector(configurator);
	}

	private Injector(final ModuleConfigurator configurator) {
		injector = Guice.createInjector(configurator);
		this.configurator = configurator;
	}

	public static void injectMembers(final Object instance) {
		if (INSTANCE != null) {
			INSTANCE.injector.injectMembers(instance);
		}
	}

	public static void regist(final Injectable instance) {
		if (INSTANCE != null) {
			INSTANCE.configurator.regist(instance);
		}
	}

	public static <T> T getInstance(final Class<T> cls) {
		return INSTANCE != null ? INSTANCE.injector.getInstance(cls) : null;
	}
}
