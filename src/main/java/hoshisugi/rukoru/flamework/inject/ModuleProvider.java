package hoshisugi.rukoru.flamework.inject;

import com.google.inject.Provider;

public class ModuleProvider<T extends Injectable> implements Provider<T> {

	private T instance;

	public static <T extends Injectable> ModuleProvider<T> of(final Class<T> cls) {
		return new ModuleProvider<>();
	}

	public void regist(final T instance) {
		this.instance = instance;
	}

	@Override
	public T get() {
		return instance;
	}
}
