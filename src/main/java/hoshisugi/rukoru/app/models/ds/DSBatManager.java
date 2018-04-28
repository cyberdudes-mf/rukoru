package hoshisugi.rukoru.app.models.ds;

import java.util.function.Consumer;

import hoshisugi.rukoru.framework.cli.CLIState;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;

public class DSBatManager extends DSManagerBase {

	@Override
	public void startServer(final DSSetting dsSetting, final DSLogWriter writer, final Consumer<CLIState> callback) {
		ConcurrentUtil.run(() -> service.startServerBat(dsSetting, writer, callback));
	}

	@Override
	public void stopServer(final DSSetting dsSetting, final Consumer<CLIState> callback) {
		ConcurrentUtil.run(() -> service.stopServerBat(dsSetting, callback));
	}

	@Override
	public void startStudio(final DSSetting dsSetting, final DSLogWriter writer, final Consumer<CLIState> callback) {
		ConcurrentUtil.run(() -> service.startStudioBat(dsSetting, writer, callback));
	}

	@Override
	public void stopStudio(final DSSetting dsSetting, final Consumer<CLIState> callback) {
		ConcurrentUtil.run(() -> service.stopStudioBat(dsSetting, callback));
	}

}
