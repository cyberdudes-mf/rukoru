package hoshisugi.rukoru.app.models.ds;

public interface DSManager {

	static DSManager getManager(final DSEntry entry) {
		DSManager manager;
		switch (entry.getDsSetting().getExecutionType()) {
		case SERVICE:
			manager = new DSServiceManager(entry);
			break;
		case EXE:
			manager = new DSExeManager(entry);
			break;
		case BAT:
			manager = new DSBatManager(entry);
			break;
		default:
			throw new IllegalStateException("何できどうすればいいか分かりません。。。");
		}
		return manager;
	}

	void startServer();

	void stopServer();

	void startStudio();

	void stopStudio();

	void startBoth();

	void stopBoth();

}
