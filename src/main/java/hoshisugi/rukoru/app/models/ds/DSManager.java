package hoshisugi.rukoru.app.models.ds;

public interface DSManager {

	static DSManager getManager(final DSEntry entry) {
		switch (entry.getDsSetting().getExecutionType()) {
		case SERVICE:
			return new DSServiceManager(entry);
		case EXE:
			return new DSExeManager(entry);
		case BAT:
			return new DSBatManager(entry);
		}
		throw new IllegalStateException("起動方法が指定されていません。");
	}

	void startServer();

	void stopServer();

	void startStudio();

	void stopStudio();

	void startBoth();

	void stopBoth();

}
