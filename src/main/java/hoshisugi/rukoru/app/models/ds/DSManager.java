package hoshisugi.rukoru.app.models.ds;

import java.util.function.Consumer;

import hoshisugi.rukoru.app.enums.ExecutionType;
import hoshisugi.rukoru.framework.cli.CLIState;

public interface DSManager {

	static DSManager getManager(final ExecutionType executionType) {
		DSManager manager;
		switch (executionType) {
		case SERVICE:
			manager = new DSServiceManager();
			break;
		case EXE:
			manager = new DSExeManager();
			break;
		case BAT:
			manager = new DSBatManager();
			break;
		default:
			throw new IllegalStateException("何できどうすればいいか分かりません。。。");
		}
		return manager;
	}

	void startServer(DSSetting dsSetting, DSLogWriter writer, Consumer<CLIState> callback);

	void stopServer(DSSetting dsSetting, Consumer<CLIState> callback);

	void startStudio(DSSetting dsSetting, DSLogWriter writer, Consumer<CLIState> callback);

	void stopStudio(DSSetting dsSetting, Consumer<CLIState> callback);
}
