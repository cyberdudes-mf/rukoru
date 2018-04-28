package hoshisugi.rukoru.app.models.ds;

import java.util.function.Consumer;

import hoshisugi.rukoru.framework.cli.CLIState;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;

public class DSExeManager extends DSManagerBase {

	@Override
	public void startServer(final DSSetting dsSetting, final DSLogWriter writer, final Consumer<CLIState> callback) {
		ConcurrentUtil.run(() -> service.startServerExe(dsSetting, writer, callback));
	}

	@Override
	public void stopServer(final DSSetting dsSetting, final Consumer<CLIState> callback) {
		ConcurrentUtil.run(() -> service.stopServerExe(dsSetting, callback));
	}

	@Override
	public void startStudio(final DSSetting dsSetting, final DSLogWriter writer, final Consumer<CLIState> callback) {
		ConcurrentUtil.run(() -> service.startStudioExe(dsSetting, writer, callback));
	}

	@Override
	public void stopStudio(final DSSetting dsSetting, final Consumer<CLIState> callback) {
		ConcurrentUtil.run(() -> service.stopStudioExe(dsSetting, callback));
	}

}
