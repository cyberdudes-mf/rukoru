package hoshisugi.rukoru.framework.inject;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public abstract class ModuleConfigurator extends AbstractModule {

	private final Map<Class<?>, ModuleProvider<? extends Injectable>> providers = new HashMap<>();

	public <T extends Injectable> void regist(final T instance) {
		@SuppressWarnings("unchecked")
		final ModuleProvider<T> provider = (ModuleProvider<T>) getModuleProvider(instance.getClass());
		if (provider != null) {
			provider.regist(instance);
		}
	}

	protected <T extends Injectable> void provide(final Class<T> cls) {
		final ModuleProvider<T> provider = ModuleProvider.of(cls);
		providers.put(cls, provider);
		bind(cls).toProvider(provider).in(Singleton.class);
	}

	protected <T extends Injectable, I extends T> void provide(final Class<T> cls, final I instance) {
		final ModuleProvider<T> provider = ModuleProvider.of(cls);
		provider.regist(instance);
		providers.put(cls, provider);
		bind(cls).toProvider(provider).in(Singleton.class);
	}

	@SuppressWarnings("unchecked")
	<T extends Injectable> ModuleProvider<T> getModuleProvider(final Class<T> cls) {
		return (ModuleProvider<T>) providers.get(cls);
	}
}
